file=open('output','r')
List=file.readlines()
popular = List[0].split("\t")[0]
print popular
