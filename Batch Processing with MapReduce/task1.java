import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ngram {
	public static class Map1 extends Mapper<Object,Text, Text,IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] a = value.toString().split("[^a-zA-Z]+");
			//remove null
			ArrayList<String> list = new ArrayList<>();
			for(String c:a){
				list.add(c);
			}
			list.remove("");
			
			//from 1 word to 6 words
			for(int i = 1; i <6; i++){
				//get the sub string from the input
				for(int k = 0; k < list.size()+1 - i; k++ ){
					StringBuilder str = new StringBuilder("");
					//write to map output
					for(int j = 0; j < i; j++){
						if(j>0){
							str = str.append(" ");
							str = str.append(list.get(k+j));
						}
						else
							str = str.append(list.get(k+j));
					}
					word.set(str.toString().toLowerCase());
					context.write(word, one);
				}
			}
		}
	}

	public static class Reduce1 extends Reducer<Text,IntWritable,Text,IntWritable> {
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			//count the number
			for (IntWritable val : values) {
				sum += val.get();
			}
			
			//if(sum >2){
				context.write(key, new IntWritable(sum));
			//}	
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "wordcount");
		job.setJarByClass(ngram.class);
		job.setMapperClass(Map1.class);
		job.setCombinerClass(Reduce1.class);
		job.setReducerClass(Reduce1.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}