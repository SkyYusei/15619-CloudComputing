file=open('temp','r')
List=file.readlines()
lp = List[len(List)-1].split("\t")   #get the last one of sorted output
print lp[1]+"\t"+lp[0]
