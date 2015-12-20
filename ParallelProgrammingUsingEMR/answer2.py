file=open('temp','r')
List=file.readlines()
popular = List[0].split("\t")   #get the first line of sorted output
print popular[1]+'\t'+popular[0]
