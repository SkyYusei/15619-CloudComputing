#!/usr/bin/env python
import sys
current_title = None
#current_date = None
current_num = 0;
count_dic = {}
def init():
	for i in range(20150801,20150832):
		count_dic[str(i)]=0
	
init()
#for line in sys.stdin:
for line in sys.stdin:
	
	line = line.strip()
	tdn = line.split('\t')
	title = tdn[0]
	date = tdn[1]
	num = tdn[2]
	#print title
	try:
		num = int(num)
	except ValueError:
		continue

	if current_title == title:
		count_dic[str(date)] +=num
	else: 
		if current_title:
			sum = 0
			for i in range(20150801,20150832):
				sum += count_dic[str(i)]
				#print sum
			if sum >100000:
				printline = str(sum) + "\t" + current_title
				for i in range(20150801,20150832):
					printline += "\t"+str(i)+":"+str(count_dic[str(i)])
				printline = printline
				#openfile.write(printline)
				print printline
		init()
		current_title = title
		#print title+"123123213"
		count_dic[str(date)] = num
if current_title == title:
	sum = 0
	for i in range(20150801,20150832):
		sum += count_dic[str(i)]
	if sum > 100000:
		printline = str(sum) + "\t" +current_title
		for i in range(20150801,20150832):
			printline += "\t"+str(i)+":"+str(count_dic[str(i)])
		printline = printline 
		#openfile.write(printline)
		print printline
	init()




