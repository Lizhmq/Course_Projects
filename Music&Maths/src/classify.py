#!/usr/bin/env python3
import os
import numpy as np
from sklearn.svm import SVC
import tensorflow as tf

readpath = "../mfcc/"
files = os.listdir(readpath)

train = 0.8

files1 = [f for f in files if 'b' in f]
files2 = [f for f in files if 'p' in f]
files3 = [f for f in files if 'a' in f]

np.random.shuffle(files1)
np.random.shuffle(files2)
np.random.shuffle(files3)

test_files = files1[(int)(len(files1)*train):] \
			+ files2[(int)(len(files2)*train):] \
			+ files3[(int)(len(files3)*train):]
train_files = files1[0:(int)(len(files1)*train)] \
			+ files2[0:(int)(len(files2)*train)] \
			+ files3[0:(int)(len(files3)*train)]
# print(len(test_files))
# print(len(train_files))

test_data = []
test_Y = []
train_data = []
train_Y = []

for file in test_files:
	# f = open(readpath + file, "r")
	# array = eval(f.read())
	array = np.loadtxt(readpath + file)
	array.shape = 1, -1
	test_data.append(array[0])
	# NO = 2
	NO = [0, 0, 1]
	if 'b' in file:
		# NO = 0
		NO = [1, 0, 0]
	if 'p' in file:
		# NO = 1
		NO = [0, 1, 0]
	test_Y.append(NO)

for file in train_files:
	# f = open(readpath + file, "r")
	# array = eval(f.read())
	array = np.loadtxt(readpath + file)
	array.shape = 1, -1
	train_data.append(array[0])
	# NO = 2
	NO = [0, 0, 1]
	if 'b' in file:
		# NO = 0
		NO = [1, 0, 0]
	if 'p' in file:
		# NO = 1
		NO = [0, 1, 0]
	train_Y.append(NO)

train_Y = np.array(train_Y)
train_Y.shape = -1, 1
# train_Y.shape = -1, 3
test_Y = np.array(test_Y)
test_Y.shape = -1, 1
# test_Y.shape = -1, 3
train_data = np.array(train_data)
test_data = np.array(test_data)

# clf = SVC(kernel='rbf', gamma='auto')
# clf.fit(train_data, train_Y)
# y = clf.predict(test_data)
# print(y)
# print(test_Y)

def add_layer(inputs, in_size, out_size, act=tf.nn.sigmoid):
	weights = tf.Variable(tf.random_normal([in_size, out_size]))
	biases = tf.Variable(tf.random_normal([1, out_size]))
	return act(tf.matmul(inputs, weights) + biases)

# xs = 
input_size = len(train_data[0])
xs = tf.placeholder(tf.float32, [None, input_size])
ys = tf.placeholder(tf.float32, [None, 3])
pred = add_layer(xs, input_size, 3)

# loss = tf.reduce_mean(tf.reduce_sum(tf.square(ys - pred), ))
loss = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(\
		labels=ys, logits=pred))

train_step = tf.train.GradientDescentOptimizer(0.1).minimize(loss)

init = tf.global_variables_initializer()
sess = tf.Session()
sess.run(init)
with sess.as_default():
	for i in range(4000):
		sess.run(train_step, feed_dict={xs: train_data, ys: train_Y})
	#print(pred.eval(feed_dict={xs: train_data, ys: train_Y}))
	#print(loss.eval(feed_dict={xs: train_data, ys: train_Y}))
	p = pred.eval(feed_dict = {xs: train_data, ys: train_Y})
	z = 0.0
	for i in range(len(train_Y)):
			if (p[i] == train_Y[i]).any():
					z = z + 1
	print("accuracy on train data:", z / len(train_Y))
	z = 0.0
	p = pred.eval(feed_dict = {xs: test_data, ys: test_Y})
	for i in range(len(test_Y)):
			if (p[i] == test_Y[i]).any():
					z = z + 1
	print("accuracy on test data:", z / len(test_Y))
