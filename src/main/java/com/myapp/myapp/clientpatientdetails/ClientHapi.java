package com.myapp.hapiclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Patient.ContactComponent;
import org.hl7.fhir.dstu3.model.Patient.PatientCommunicationComponent;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Practitioner.PractitionerQualificationComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class ClientHapi {
	FhirContext ctx = FhirContext.forDstu3();
	IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/hapi-jpa-mysql/baseDstu3/");

	public void createPatient() {

		// FhirContext ctx = FhirContext.forDstu3();
		Patient patient = new Patient();
		patient.addIdentifier().setSystem("http://acme.org/mrns").setValue("67866");
		List<StringType> givenNameList = new ArrayList<StringType>();
		givenNameList.add(new StringType("JJJJ"));
		givenNameList.add(new StringType("YYYY"));
		patient.addName().setGiven(givenNameList).setFamily("F2");
		patient.setGender(AdministrativeGender.MALE);
		List<StringType> addressLineList = new ArrayList<StringType>();
		addressLineList.add(new StringType("Plot No#9, New #4/145"));
		addressLineList.add(new StringType("Rangarajapuram first main road"));
		addressLineList.add(new StringType("Santhosapuram"));

		patient.addAddress().setLine(addressLineList).setState("Tamilnadu").setCity("Chennai").setCountry("India")
				.setPostalCode("600073");
		patient.getMaritalStatus().setText("Single");
		PatientCommunicationComponent primaryLanguage = new PatientCommunicationComponent();
		primaryLanguage.setPreferred(true);
		primaryLanguage.setLanguage(new CodeableConcept().addCoding(new Coding().setSystem("http://tools.ietf.org/html/bcp47").setCode("en")));
		patient.addCommunication(primaryLanguage);
		patient.addCommunication().setLanguage(new CodeableConcept().addCoding(new Coding().setSystem("http://tools.ietf.org/html/bcp47").setCode("te")));
		try {
			patient.setBirthDate((new SimpleDateFormat("dd-MM-yyyy")).parse("04-11-1982"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ContactComponent contact = new ContactComponent();
		List<Coding> contactTypes = new ArrayList<Coding>();
		Coding billingContact = new Coding();
		billingContact.setSystem("http://hl7.org/fhir/v2/0131");
		billingContact.setCode("BP");
		Coding emergencyContact = new Coding();
		emergencyContact.setSystem("http://hl7.org/fhir/v2/0131");
		emergencyContact.setCode("C");
		contactTypes.add(billingContact);
		contactTypes.add(emergencyContact);
		contact.addRelationship().setCoding(contactTypes);
		contact.addTelecom().setSystem(ContactPointSystem.EMAIL).setValue("xyz@gmail.com");
		contact.addTelecom().setSystem(ContactPointSystem.PHONE).setValue("+91123456789");
		contact.setName(new HumanName().addGiven("Revathy").setFamily("T"));
		contact.setGender(AdministrativeGender.FEMALE);
		contact.setAddress(new Address().setCity("Chennai"));
		
		ContactComponent employerContact = new ContactComponent();
		employerContact.addRelationship().addCoding().setSystem("http://hl7.org/fhir/v2/0131").setCode("E");
		employerContact.addTelecom().setSystem(ContactPointSystem.EMAIL).setValue("ddddd@gmail.com");
		employerContact.addTelecom().setSystem(ContactPointSystem.PHONE).setValue("+1123456789");
		employerContact.setName(new HumanName().addGiven("CJ"));
		employerContact.setGender(AdministrativeGender.MALE);
		employerContact.setAddress(new Address().setCountry("USA"));

		List<ContactComponent> contactList = new ArrayList<ContactComponent>();
		contactList.add(contact);
		contactList.add(employerContact);
		patient.setContact(contactList);
		patient.addTelecom().setSystem(ContactPointSystem.EMAIL).setValue("abc@gmail.com");
		patient.addTelecom().setSystem(ContactPointSystem.PHONE).setValue("+9112122229");
		patient.addGeneralPractitioner(new Reference("Practitioner/166"));
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
	public void createPractioner(){
		Practitioner practitioner = new Practitioner();


		practitioner.addIdentifier().setSystem("http://acme.org/mrns").setValue("11111");
		List<StringType> givenNameList = new ArrayList<StringType>();
		givenNameList.add(new StringType("Doctor"));
		givenNameList.add(new StringType("one"));
		practitioner.addName().setGiven(givenNameList).setFamily("FD");
		practitioner.setGender(AdministrativeGender.MALE);
		List<StringType> addressLineList = new ArrayList<StringType>();
		addressLineList.add(new StringType("Plot No#9, New #4/145"));
		addressLineList.add(new StringType("Rangarajapuram first main road"));
		addressLineList.add(new StringType("Santhosapuram"));

		practitioner.addAddress().setLine(addressLineList).setState("Tamilnadu").setCity("Chennai").setCountry("India")
				.setPostalCode("600073");
		practitioner.addCommunication(new CodeableConcept().addCoding(new Coding().setSystem("http://tools.ietf.org/html/bcp47").setCode("en")));
		try {
			practitioner.setBirthDate((new SimpleDateFormat("dd-MM-yyyy")).parse("04-11-1982"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
		practitioner.addTelecom().setSystem(ContactPointSystem.EMAIL).setValue("rrr@gmail.com");
		practitioner.addTelecom().setSystem(ContactPointSystem.PHONE).setValue("+913333323232");
		PractitionerQualificationComponent qualification = new PractitionerQualificationComponent();
		qualification.setCode(new CodeableConcept().addCoding(new Coding().setSystem("http://hl7.org/fhir/v2/0360/2.7").setCode("MD")));
		practitioner.addQualification(qualification);
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.TRANSACTION);
		bundle.addEntry().setResource(practitioner).getRequest().setUrl("Practitioner").setMethod(HTTPVerb.POST);
		Bundle resp = client.transaction().withBundle(bundle).execute();
		// Log the response
		System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resp));

	
	}

	public static void main(String[] args) {
		ClientHapi cli = new ClientHapi();
		cli.createPatient();
		//cli.createPractioner();
		// cli.viewPatient();

	}

}