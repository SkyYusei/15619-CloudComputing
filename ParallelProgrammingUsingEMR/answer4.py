file = open("temp","r")
for line in file:
	content = line.split("\t")
	dateview =int( content[2].split(":")[1]) #get the 08012015's view 
	if dateview == 0:   #if view == 0
		print content[1]
		break
	
