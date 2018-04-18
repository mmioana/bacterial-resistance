package com.google.gwt.bacterialresistance.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

public class FileUploadServlet extends HttpServlet {
	
	private static final String UPLOAD_DIRECTORY = "D:\\uploaded\\";
	
	private static HashMap<String, String> fieldVals = new HashMap<String, String>();
	
	private String res;
	
	private String theFileName;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws 
	 ServletException, IOException {
	                 
	                 resp.setContentType("text/plain");
	                 resp.setHeader("Content-Disposition", "attachment; filename=output.txt");

	                 PrintWriter out = resp.getWriter();
	                 out.println(this.res);
	         }
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException{
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		
			List<FileItem> items;
			
			try{
				items = upload.parseRequest(req);
			}
			catch(FileUploadException e){
				throw new ServletException("File upload failed", e);
			}
			
			for(FileItem item : items){
				System.out.println("item = " + item);
				
				
				if (item.isFormField()){
					InputStream stream = item.getInputStream();
					String name = item.getFieldName();
					
					fieldVals.put(name, Streams.asString(stream));
					
					System.out.println("Form field " + name + " with value "
				    + fieldVals.get(name) + " detected.");
					
					continue;
				}
				
				
				String fileName = item.getName();
				if (fileName != null) {
					fileName = FilenameUtils.getName(fileName);
				}
				
				//InputStream stream = item.getInputStream();
				String name = item.getFieldName();
				
				//System.out.println(item.isFormField());

					
				File uploadedFile = new File(UPLOAD_DIRECTORY, fileName);
					if (uploadedFile.createNewFile()) {
						try {
							item.write(uploadedFile);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//resp.setStatus(HttpServletResponse.SC_CREATED);
						//resp.getWriter().print("The file was created successfully.");
						//resp.flushBuffer();

						//computeResults(fileName);
						this.theFileName = fileName;
					}
					else{
						throw new IOException("The file already exists in repository.");
					}
			    
			}
			
			computeResults();
		
	}
	
	private void computeResults(){
		
		int numberOfAntibiotics/* = 3*/;
				
				Distributii d = new Distributii(
						Integer.parseInt(fieldVals.get("numOfInfected")),
						Integer.parseInt(fieldVals.get("numOfPersons")),
						Float.parseFloat(fieldVals.get("pNiu")),
						Boolean.parseBoolean(fieldVals.get("isDirected")),
						Float.parseFloat(fieldVals.get("exposurePeriod")),
						Float.parseFloat(fieldVals.get("infectionPeriodH")),
						Float.parseFloat(fieldVals.get("infectionPeriodR")),
						Float.parseFloat(fieldVals.get("infectionPeriodF")),
						Float.parseFloat(fieldVals.get("hospitalizationPeriodR")),
						Float.parseFloat(fieldVals.get("hospitalizationPeriodF")),
						Float.parseFloat(fieldVals.get("deathPeriod")),
						Float.parseFloat(fieldVals.get("immunePeriod")));
				
				d.setBetaI(Float.parseFloat(fieldVals.get("betaI")));
				d.setBetaH(Float.parseFloat(fieldVals.get("betaH")));
				d.setBetaF(Float.parseFloat(fieldVals.get("betaF")));
				d.setTheta1(Float.parseFloat(fieldVals.get("theta")));
				d.setDelta1(Float.parseFloat(fieldVals.get("delta1")));
				d.setDelta2(Float.parseFloat(fieldVals.get("delta2")));
				
				//set parameters for bacterial resistance
				d.setBeta(Float.parseFloat(fieldVals.get("beta")));
				d.setGamma(Float.parseFloat(fieldVals.get("gamma")));
				d.setTau(Float.parseFloat(fieldVals.get("tau")));
		
		ReadFromFile rd = new ReadFromFile("d:\\uploaded\\" + this.theFileName);
		
		ArrayList<Disease> dis = rd.readFromCSVFile();
		
		String results = "";
		
		for(Disease disease : dis){
				
				//System.out.println(disease.getRes());
				numberOfAntibiotics = Disease.getNumOfResGenes();
				
				d.setNumOfAntibiotics(numberOfAntibiotics);
				
				d.setResistanceFreq(disease.getRes());
				
				d.generateInitialGraph();
				
				results += d.toString();
				
				results +="Evaluting network in time..........";
				
				d.evaluateNetworkInTime(20);
				
				results += d.toString();
				
				break;
			}
		
		this.res = results;
		
	}

}
