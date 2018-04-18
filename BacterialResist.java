package com.google.gwt.bacterialresistance.client;

import com.google.gwt.bacterialresistance.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BacterialResist implements EntryPoint {
	
	
	private void addLabelAndText(String labelName, String fieldName, String initVal, HorizontalPanel hp){
		Label lb = new Label(labelName);
		hp.add(lb);
		TextBox tb = new TextBox();
		tb.setName(fieldName);
		tb.setValue(initVal);
		hp.add(tb);
	}
	
	public void onModuleLoad(){
		final FormPanel form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setAction("upload");
		
		VerticalPanel panel = new VerticalPanel();
		form.setWidget(panel);
		
		FileUpload upload = new FileUpload();
		upload.setName("fisier");
		panel.add(upload);
		
		HorizontalPanel hp1 = new HorizontalPanel();
		panel.add(hp1);
		
		addLabelAndText("Number of infected:", "numOfInfected", "5", hp1);
		addLabelAndText("Number of persons:", "numOfPersons", "100", hp1);
		addLabelAndText("Probability of initial connection between S-I:", "pNiu", "0.54f", hp1);
		
		//panel.add(numOfInfected);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		panel.add(hp2);
		HorizontalPanel hp3 = new HorizontalPanel();
		panel.add(hp3);
		HorizontalPanel hp4 = new HorizontalPanel();
		panel.add(hp4);
		
		HorizontalPanel hp5 = new HorizontalPanel();
		panel.add(hp5);
		
		HorizontalPanel hp6 = new HorizontalPanel();
		panel.add(hp6);
		
		HorizontalPanel hp7 = new HorizontalPanel();
		panel.add(hp7);
		
		addLabelAndText("Is directed graph?(true/false)", "isDirected", "false", hp2);
		addLabelAndText("Exposure period", "exposurePeriod", "2.5f", hp2);
		addLabelAndText("Infection period before hospitalization", "infectionPeriodH", "4.0f", hp2);
		
		addLabelAndText("Infection period before immunization", "infectionPeriodR", "2.5f", hp3);
		addLabelAndText("Infection period before death", "infectionPeriodF", "2.88f", hp3);
		
		addLabelAndText("Hospitalization period before immunization", "hospitalizationPeriodR", "3.1f", hp4);
		addLabelAndText("Hospitalization period before death", "hospitalizationPeriodF", "2.0f", hp4);
		
		addLabelAndText("Death period before immunization", "deathPeriod", "2.0f", hp5);
		addLabelAndText("Immune period before susceptible again", "immunePeriod", "1.0f", hp5);
		
		addLabelAndText("betaI", "betaI", "0.25f", hp6);
		addLabelAndText("betaH", "betaH", "0.3f", hp6);
		addLabelAndText("betaF", "betaF", "0.45f", hp6);
		
		addLabelAndText("theta", "theta", "0.86f", hp6);
		
		addLabelAndText("delta1", "delta1", "0.15f", hp6);
		addLabelAndText("delta2", "delta2", "0.29f", hp6);
		
		//parameters for bacterial resistance
		addLabelAndText("beta", "beta", "0.9f", hp7);
		addLabelAndText("gamma", "gamma", "0.03f", hp7);
		addLabelAndText("tau", "tau", "0.5f", hp7);
		
		
		
		panel.add(new Button("Submit file and fields", new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				form.submit();
			}
		}));
		
		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
		             public void onSubmitComplete(SubmitCompleteEvent event) {

		             //Window.alert(event.getResults());
		            	 event.getResults();
	                                
			 }
		});
		
		RootPanel.get("gwtContainer").add(form);
		String link = "upload";
        RootPanel.get().add(new HTML("<a href=\"" + link + "\">Download File</a>"));
	}
	
	public void computeResults(){
		
	}


}