package com.myapp.hapiclient;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;

public class HapiClientFinal {
	FhirContext ctx = FhirContext.forDstu3();
	IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/hapi-jpa-mysql/baseDstu3/");

	public void createPatient() {

		// FhirContext ctx = FhirContext.forDstu3();
		Patient patient = new Patient();
		patient.addIdentifier().setSystem("http://acme.org/mrns").setValue("12344");
		patient.addName().setFamily("NoOne1");
		patient.setGender(AdministrativeGender.MALE);
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.TRANSACTION);
		bundle.addEntry().setResource(patient).getRequest().setUrl("Patient").setMethod(HTTPVerb.POST);
		Bundle resp = client.transaction().withBundle(bundle).execute();
		// Log the response
		System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resp));
	}

	public void viewPatient() {
		IBaseBundle resp = client.search().forResource(Patient.class).prettyPrint().execute();
		System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resp));
	}

	public void createObservation() {
		Observation observation = new Observation();
		observation.setStatus(ObservationStatus.FINAL);

		observation.getCode().addCoding().setSystem("http://loinc.org").setCode("789-8")
				.setDisplay("Erythrocytes [#/volume] in Blood by Automated count");
		observation.setValue(new Quantity().setValue(4.12).setUnit("10 trillion/L")
				.setSystem("http://unitsofmeasure. tempoorg").setCode("10*12/L"));
		observation.setSubject(new Reference("Patient/1"));

		// Create a bundle that will be used as a transaction
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.TRANSACTION);
		bundle.addEntry().setResource(observation).getRequest().setUrl("Observation").setMethod(HTTPVerb.POST);

		// Log the request
		FhirContext ctx = FhirContext.forDstu3();
		System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle));

		// Create a client and post the transaction to the server
		IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/hapi-jpa-mysql/baseDstu3/");
		// Bundle resp = client.transaction().withBundle(bundle).execute();
		IBaseBundle resp = client.search().forResource(Observation.class)
				.where(new ReferenceClientParam("subject").hasId("1")).prettyPrint().encodedXml().execute();
		// Log the response
		System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resp));
	}

	public void viewObservation() {
		Observation observation = new Observation();
		observation.setStatus(ObservationStatus.FINAL);

		observation.getCode().addCoding().setSystem("http://loinc.org").setCode("789-9")
				.setDisplay("Erythrocytes [#/volume] in Blood by Automated count");
		observation.setValue(new Quantity().setValue(4.12).setUnit("7 trillion/L")
				.setSystem("http://unitsofmeasure. tempoorg").setCode("10*12/L"));
		observation.setSubject(new Reference("Patient/1"));

		// Create a bundle that will be used as a transaction
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.TRANSACTION);
		bundle.addEntry().setResource(observation).getRequest().setUrl("Observation").setMethod(HTTPVerb.POST);

		// Log the request
		FhirContext ctx = FhirContext.forDstu3();
		System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle));

		// Create a client and post the transaction to the server
		IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/hapi-jpa-mysql/baseDstu3/");
		Bundle resp = client.transaction().withBundle(bundle).execute();

		// Log the response
		System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resp));

	}

	public static void main(String[] args) {
		HapiClientFinal cli = new HapiClientFinal();
		cli.createPatient();
		cli.viewPatient();
		 cli.createObservation();
		 cli.viewObservation();

	}

}