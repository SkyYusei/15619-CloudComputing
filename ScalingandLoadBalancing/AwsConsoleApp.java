import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.simpleemail.model.Content;

public class AwsConsoleApp {
    	
    public static void main(String[] args) throws Exception {
        String submissioncode = new String(readAllBytes(get("submissioncode")));
        System.out.println(submissioncode);
    	System.out.println("Creating Security Group");
    	CreateSecurityGroup csrg = new CreateSecurityGroup();
    	csrg.createSecurityGroup(); //CREATING SECURITY GROUP "ALLPORTS"
    	System.out.println("Creatng Instances");
        CreateInstance createLG = new CreateInstance();  //launch load generator
        createLG.createInstance("ami-4389fb26", "m3.medium"); //launch data center
        CreateInstance createDC = new CreateInstance();
        createDC.createInstance("ami-abb8cace", "m3.medium");
       
        GetInstanceInfo LGinfo = new GetInstanceInfo();  //get lg's info DNS  status
        GetInstanceInfo DCinfo = new GetInstanceInfo(); //get dc's info DNS status
                
        LGinfo.getInstanceInfo(createLG.instanceId);
        System.out.println("Load Generator Finished"+":"+createLG.instanceId+"\t"+LGinfo.DNS);
        
        DCinfo.getInstanceInfo(createDC.instanceId);
        System.out.println("Date Center Finished   "+":"+createDC.instanceId+"\t"+DCinfo.DNS);
        String testNum = null;
        
        String content1 = null;
        while(content1 == null){   //add submission code to lg
        try {
        	URL urlsubcode = new URL("http://"+URLEncoder.encode(LGinfo.DNS,"UTF-8")+"/password?passwd="+URLEncoder.encode(submissioncode,"UTF-8"));
        	HttpURLConnection urlcon = (HttpURLConnection)urlsubcode.openConnection();
        	urlcon.connect();
        	//f1 =false;
            InputStream a = urlcon.getInputStream();
            System.out.println("http://"+LGinfo.DNS+"/password?passwd=uS0WkBgqRUVJbiHK6Vus8VeLb9KziALm");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(a));
            StringBuffer bs = new StringBuffer();
            String l = null;
            while((l=buffer.readLine())!=null){
            	bs.append(l).append("/n");
            }
            content1 = bs.toString();
            System.out.println(bs.toString());
		} catch (Exception e) {
			//f1 = true;
			content1 = null;
		}
        }

        
        Thread.sleep(100000);
        String content = null;
        while(content == null){  //add first dc to lg
        try {
        	
        	URL urlsubdc = new URL("http://"+URLEncoder.encode(LGinfo.DNS,"UTF-8")+"/test/horizontal?dns="+URLEncoder.encode(DCinfo.DNS,"UTF-8"));
            HttpURLConnection urlcon2 = (HttpURLConnection)urlsubdc.openConnection();
        	urlcon2.connect();
        	System.out.println("http://"+LGinfo.DNS+"/test/horizontal?dns="+DCinfo.DNS);
        	
			
			InputStream b = urlcon2.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(b));
            StringBuffer bs = new StringBuffer();
            String l = null;
            while((l=buffer.readLine())!=null){
            	bs.append(l).append("/n");
            }
            content = bs.toString();
            System.out.println(content);
            testNum = content.split("<")[8].split("\\.")[1];
            System.out.println(testNum);   //get  test num
		} catch (Exception e) {
			// TODO: handle exception
			content = null;
		}
        };
        
        //Thread.sleep(100000);
        boolean flag = true;
        while(flag){
        	boolean testf = true;
        	//String content3 = null;
            while(testf){
            try {
            	URL urllog = new URL("http://"+LGinfo.DNS+"/log?name=test."+testNum+".log");   //get log 
                HttpURLConnection urlcon3 = (HttpURLConnection)urllog.openConnection();
            	urlcon3.connect();
    			testf =false;
//    			InputStream b = urlcon3.getInputStream();
//                BufferedReader buffer1 = new BufferedReader(new InputStreamReader(b));
//                StringBuffer bs = new StringBuffer();
//                String ll = null;
//                while((ll=buffer1.readLine())!=null){
//                	bs.append(ll).append("/n");
//                }
//                content3 = bs.toString();
//                System.out.println(content3);
    			System.out.println("http://"+LGinfo.DNS+"/log?name=test."+testNum+".log");
    			InputStream c = urlcon3.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(c));
                //StringBuffer bs = new StringBuffer();
                String l = null;
                String page = null;
                //String secondlastline = null;
                double rpsNum = 0;
                Pattern pat = Pattern.compile("ec2[^=]*\\.com=([.\\d]*)");
                while((l=buffer.readLine())!=null){
                	page +=l;
                };
                page = page.substring(page.lastIndexOf("[Minute"));
                Matcher ma = pat.matcher(page);
                while(ma.find()){
                	rpsNum += Double.parseDouble(ma.group(1));	    //get rps num
                }
                 //System.out.println(secondlastline);
                System.out.println(rpsNum);
                
                 // add if rps <4000
                if(rpsNum < (double)4000){
                	CreateInstance moreDC = new CreateInstance();
                	moreDC.createInstance("ami-abb8cace", "m3.medium");
                	GetInstanceInfo moreDCinfo = new GetInstanceInfo();        
                    moreDCinfo.getInstanceInfo(moreDC.instanceId);
                    System.out.println("moreDC created!!");
                    Thread.sleep(100000);
                    //boolean f3 = false;
                    String content4 = null;
                    while(content4 == null){
                    try {
                    	URL urladd = new URL("http://"+LGinfo.DNS+"/test/horizontal/add?dns="+moreDCinfo.DNS);  //add new dc to lg
                        HttpURLConnection urlcon4 = (HttpURLConnection)urladd.openConnection();
                    	urlcon4.connect();
                    	InputStream b1 = urlcon4.getInputStream();
                        BufferedReader buffer2 = new BufferedReader(new InputStreamReader(b1));
                        StringBuffer bs1 = new StringBuffer();
                        String l1 = null;
                        while((l1=buffer2.readLine())!=null){
                        	bs1.append(l1).append("/n");
                        }
                        content4 = bs1.toString();
                        System.out.println(content4);
            		
            			System.out.println("http://"+LGinfo.DNS+"/test/horizontal/add?dns="+moreDCinfo.DNS);
            		} catch (Exception e) {
            			// TODO: handle exception
            			content4 = null;
            		}
                    };
                }
                else flag = false;
                
                
    		} catch (Exception e) {
    			// TODO: handle exception
    			testf = true;
    		}
            }
        	
        }
   
    }

}



