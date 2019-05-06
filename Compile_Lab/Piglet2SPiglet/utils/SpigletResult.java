package utils;

public class SpigletResult {
        public String result;
        public boolean simple;

        public SpigletResult(String result, boolean simple) {
                this.result = result;
                this.simple = simple;
        }

        public String toString() {
                return this.result;
        }

        public boolean isTemp() {
                return this.result.startsWith("TEMP");
        }
        
        public boolean isSimple() {
                return this.simple;
        }
}