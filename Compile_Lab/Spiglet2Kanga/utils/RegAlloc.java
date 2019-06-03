package utils;
import java.util.*;
/*
 * 	RegAlloc:
 * 	
 * 		Allocate registers for Temp
 * 		based on the graph and usage infomation of Temp
 * 		
 * 		iterate to calculate live interval of Temp
 * 		then do linear scan algorithm to allocate register
 */
public class RegAlloc {
    HashMap<String, Method> mMethod;
    Graph curGraph;
    Method curMethod;
    public RegAlloc(HashMap<String, Method> m) {
        mMethod = m;
    }
    void LiveAnalyze() {
        boolean going = true;
        int size = curGraph.Vertexs.size();
		// Iterate until convergence
		while (going) {
			going = false;
			for (int vid = size - 1; vid >= 0; vid--) {
				GraphVertex currVertex = curGraph.VertexMap.get(vid);
				for (GraphVertex next : currVertex.Succ)
					currVertex.Out.addAll(next.In);

				HashSet<Integer> newIn = new HashSet<Integer>();
				// 'Out' - 'Def' + 'Use'
				newIn.addAll(currVertex.Out);
				newIn.removeAll(currVertex.Def);
				newIn.addAll(currVertex.Use);
				// 'In' changes, doesn't converge
				if (!currVertex.In.equals(newIn)) {
					currVertex.In = newIn;
					going = true;
				}
			}
		}
    }
    void GetLiveInterval() {
        int size = curGraph.VertexMap.size();

		// update 'end' of intervals
		for (int vid = 0; vid < size; vid++) {
			GraphVertex currVertex = curGraph.VertexMap.get(vid);
			for (Integer tempid : currVertex.In)
				curMethod.mTemp.get(tempid).end = vid;
			for (Integer tempid : currVertex.Out)
				curMethod.mTemp.get(tempid).end = vid;
		}

		for (Interval interval : curMethod.mTemp.values()) {
			for (int callPos : curGraph.CallPos) {
				// alive when calling other method
				// had better use callee-saved regs
				if (interval.begin < callPos && interval.end > callPos)
					interval.save = true;
			}
		}
    }
    public void LinearScan() {
        for (Method method : mMethod.values()) {
			curMethod = method;
			curGraph = curMethod.graph;
			LiveAnalyze();
			GetLiveInterval();

			// sort the intervals by [begin, end]
			ArrayList<Interval> intervals = new ArrayList<Interval>();
			for (Interval interval : curMethod.mTemp.values())
				intervals.add(interval);
			Collections.sort(intervals);
            
			Interval[] Tregs = new Interval[10];
			Interval[] Sregs = new Interval[8];
			for (Interval interval : intervals) {
				// last: the reg contains interval which ends last
				// empty: empty reg
				int lastT = -1, lastS = -1, emptyT = -1, emptyS = -1;
				// analyze t0-t9
				for (int regIdx = 9; regIdx >= 0; regIdx--) {
					if (Tregs[regIdx] != null) {
						// not empty
						if (Tregs[regIdx].end <= interval.begin) {
							// interval already ends
							curMethod.regT.put("TEMP " + Tregs[regIdx].tempNo, "t" + regIdx);
							Tregs[regIdx] = null;
							emptyT = regIdx;
						} else {
							if (lastT == -1 || Tregs[regIdx].end > Tregs[lastT].end)
								lastT = regIdx;
						}
					} else {
						emptyT = regIdx;
					}
				}
				// analyze s0-s7
				for (int regIdx = 7; regIdx >= 0; regIdx--) {
					if (Sregs[regIdx] != null) {
						if (Sregs[regIdx].end <= interval.begin) {
							curMethod.regS.put("TEMP " + Sregs[regIdx].tempNo, "s" + regIdx);
							Sregs[regIdx] = null;
							emptyS = regIdx;
						} else {
							if (lastS == -1 || Sregs[regIdx].end > Sregs[lastS].end)
								lastS = regIdx;
						}
					} else {
						emptyS = regIdx;
					}
				}
				// first assign T
				if (!interval.save) {
					if (emptyT != -1) {
						// assign empty T to interval
						Tregs[emptyT] = interval;
						interval = null;
					} else {
						// swap with the last T
						if (interval.end < Tregs[lastT].end) {
							Interval swapTmp = Tregs[lastT];
							Tregs[lastT] = interval;
							interval = swapTmp;
						}
					}
				}
				// then assign S
				if (interval != null) {
					if (emptyS != -1) {
						Sregs[emptyS] = interval;
						interval = null;
					} else {
						if (interval.end < Sregs[lastS].end) {
							Interval swapTmp = Sregs[lastS];
							Sregs[lastS] = interval;
							interval = swapTmp;
						}
					}
				}
				// if not assigned, spill it
				// spill temp whose interval is longest.
				// save more space for register.
				if (interval != null)
					curMethod.regSpilled.put("TEMP " + interval.tempNo, "");
			}
			for (int idx = 0; idx < 10; idx++) {
				if (Tregs[idx] != null)
					curMethod.regT.put("TEMP " + Tregs[idx].tempNo, "t" + idx);
			}
			for (int idx = 0; idx < 8; idx++) {
				if (Sregs[idx] != null)
					curMethod.regS.put("TEMP " + Sregs[idx].tempNo, "s" + idx);
			}
			// calculate stackNum:
			// contains params(>4), spilled regs, callee-saved S
			int stackIdx = (curMethod.paramNum > 4 ? curMethod.paramNum - 4 : 0) + curMethod.regS.size();
			for (String temp : curMethod.regSpilled.keySet()) {
				curMethod.regSpilled.put(temp, "SPILLEDARG " + stackIdx);
				stackIdx++;
			}
			curMethod.stackNum = stackIdx;
		}
    }
}