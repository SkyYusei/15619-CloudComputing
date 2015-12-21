//val file = sc.textFile("hdfs:///test")
val file = sc.textFile("hdfs:///data")
//get all follow list   format(1,(2,3))
var followList = file.map(line => {
	(line.split(" ")(0), line.split(" ")(1))
}).distinct().groupByKey()

//String Aarray to store all have out degree nodes
val followeeNodes = followList.keys

val followerNodes = followList.values.flatMap(a=>a).distinct()
//String Array to store all nodes
val allNodes = file.flatMap(line=>line.split(" ")).distinct()

//String Array to store nodes with no out degree
val noOutDegNodes = allNodes.subtract(followeeNodes).collect()
val noInDegNodes = allNodes.subtract(followerNodes).collect()

var noInDegNodesList = for(i<-noInDegNodes) yield(i,0.0)
//use "empty" to mark the nodes without out drgree
var noOutDegNodesList = for (i <- noOutDegNodes) yield (i, List("empty"))

//var noOutDegNodesList = sc.parallelize(noOutDegNodes.map(i=>(i.toString,Array())))

//add all nodes and its "follower" together
val wholeList = followList ++ sc.parallelize(noOutDegNodesList)
//wholeList.collect().foreach(i=>println(i))

//val total = wholeList.count().cache()
//get the num of all nodes
val numNodes = 2546953
//initial the ranks value
var ranks = wholeList.map(line => (line._1,1.0))
//ranks.collect().foreach(i=>println(i))
//start ilteration
for (i <- 1 to 10) {
	println("==============="+i+"============")
	val dcontribTotal = sc.accumulator(0.0)
	val con = wholeList.join(ranks).flatMap{
		case(a,(b,c)) => {
			//if it's no out degree nodes
			//just append a null list and add the total con
			if (b.mkString==("empty")) {
				dcontribTotal += c
				//nothing, just keep spark happy
				List()
			} else {
				//just as normal
				b.map(node => (node, c / b.size))
			}
		}
	}
	//use this to sync the accumulator
	con.count()
	val totalDL = dcontribTotal.value
	
	//get all ranks
	ranks = con.union(sc.parallelize(noInDegNodesList)).reduceByKey(_ + _).mapValues(p =>
		0.15 + 0.85 * (totalDL / numNodes + p)
	)
}
ranks.map(t=> "%s\t%s".format(t._1,t._2)).repartition(1).saveAsTextFile("s3n://ccp42p2/output2")



