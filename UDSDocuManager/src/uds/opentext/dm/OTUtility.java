package uds.opentext.dm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.opentext.ecm.api.OTAuthentication;
import com.opentext.livelink.service.core.Authentication;
import com.opentext.livelink.service.core.Authentication_Service;
import com.opentext.livelink.service.core.ContentService;
import com.opentext.livelink.service.core.ContentService_Service;
import com.opentext.livelink.service.core.FileAtts;
import com.opentext.livelink.service.docman.DocumentManagement;
import com.opentext.livelink.service.docman.DocumentManagement_Service;
import com.opentext.livelink.service.docman.MoveOptions;
import com.opentext.livelink.service.docman.Node;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.developer.JAXWSProperties;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import com.sun.xml.internal.ws.developer.WSBindingProvider;

public class OTUtility {
	
	public static final String USERNAME="otadmin@otds.admin";
	public static final String PASSWORD="SEC@crmp!2017";
	public static final String ECM_API_NAMESPACE = "urn:api.ecm.opentext.com"; // The namespace of the OTAuthentication object
	public static final String CORE_NAMESPACE = "urn:Core.service.livelink.opentext.com";
	static final Logger LOGGER = Logger.getLogger(OTUtility.class);
	
	public static String getAuthToken()
	{
		LOGGER.info("Auth Request for USER: "+USERNAME);            		
		Authentication_Service authService = null;
		Authentication authClient = null;

		// Store the authentication token
		String authToken = null;

		// Call the AuthenticateUser() method to get an authentication token
		try
		{
			// Create the Authentication service client
			authService = new Authentication_Service();
			authClient = authService.getBasicHttpBindingAuthentication();
			
			
			System.out.print("Authenticating User...");
			authToken = authClient.authenticateUser(USERNAME, PASSWORD);
			//System.out.println("Expiry date  "+authClient.getSessionExpirationDate());
			System.out.println("SUCCESS!\n");
			LOGGER.info("Auth Token: "+authToken);
		}
		catch (SOAPFaultException e)
		{
			LOGGER.error(e.getMessage());
			System.out.println("FAILED!\n");
			System.out.println(e.getFault().getFaultCode() + " : " + e.getMessage());
			
		}
		
		return authToken;
		
	}
	
	public static DocumentManagement getDocumentManagement(String authToken)
	{
		
		DocumentManagement_Service docManService = null;
		DocumentManagement docManClient = null;
		OTAuthentication otAuth = null;
		SOAPHeader header =null;
		SOAPHeaderElement otAuthElement =null;
		SOAPElement authTokenElement = null;
		String contextID = null;	// Store the context ID for the download
		
		// We need to manually set the SOAP header to include the authentication token
		try
		{
			// Create the DocumentManagement service client
			docManService = new DocumentManagement_Service();
			docManClient = docManService.getBasicHttpBindingDocumentManagement();
			
			// Create the OTAuthentication object and set the authentication token
			otAuth = new OTAuthentication();
			otAuth.setAuthenticationToken(authToken);
			
			// Create a SOAP header
			header = MessageFactory.newInstance().createMessage().getSOAPPart().getEnvelope().getHeader();

			// Add the OTAuthentication SOAP header element
			otAuthElement = header.addHeaderElement(new QName(ECM_API_NAMESPACE, "OTAuthentication"));

			// Add the AuthenticationToken SOAP element
			authTokenElement = otAuthElement.addChildElement(new QName(ECM_API_NAMESPACE, "AuthenticationToken"));
			authTokenElement.addTextNode(otAuth.getAuthenticationToken());
			
			
			// Set the SOAP header on the docManClient
			((WSBindingProvider) docManClient).setOutboundHeaders(Headers.create(otAuthElement));
			
			
		}
		catch (SOAPException  e)
		{
			LOGGER.error(e.getMessage());
			System.out.println("Failed to set authentication SOAP header!\n");
			System.out.println(e.getMessage());	
		}
		catch (SOAPFaultException e)
		{
			LOGGER.error(e.getMessage());
			System.out.println("FAILED!\n");
			System.out.println(e.getFault().getFaultCode() + " : " + e.getMessage());
		}
		
		return docManClient;
	}
	
	public static String getContext(int dataID,String authToken)
	{
		DocumentManagement docManClient=null;
		String contextID=null;
			docManClient=OTUtility.getDocumentManagement(authToken);
			System.out.print("Generating context ID...");
			contextID = docManClient.getVersionContentsContext(dataID, 0);
			System.out.println("Generated Context ID : "+contextID);
		return contextID;
	}
	
	public static ContentService getContentService(String authToken,String contextID)
	{
		OTAuthentication otAuth = null;
		ContentService_Service contentService = null;
		ContentService contentServiceClient = null;
		SOAPHeader header = null;
		SOAPHeaderElement otAuthElement = null;
		SOAPElement authTokenElement = null;
		List<Header> headers = null;
				// We need to manually set the SOAP headers to include the authentication token and context ID
		try
		{
			otAuth = new OTAuthentication();
			otAuth.setAuthenticationToken(authToken);
			
			// Create the ContentService client
			// NOTE: ContentService is the only service that requires MTOM support
			contentService = new ContentService_Service();
			contentServiceClient = contentService.getBasicHttpBindingContentService(new MTOMFeature());
			
			// Create a SOAP header
			header = MessageFactory.newInstance().createMessage().getSOAPPart().getEnvelope().getHeader();

			// Add the OTAuthentication SOAP header element
			otAuthElement = header.addHeaderElement(new QName(ECM_API_NAMESPACE, "OTAuthentication"));

			// Add the AuthenticationToken
			authTokenElement = otAuthElement.addChildElement(new QName(ECM_API_NAMESPACE, "AuthenticationToken"));
			authTokenElement.addTextNode(otAuth.getAuthenticationToken());
			
			
			// Add the ContextID SOAP header element
			SOAPHeaderElement contextIDElement = header.addHeaderElement(new QName(CORE_NAMESPACE, "contextID"));
			contextIDElement.addTextNode(contextID);

			// Set the headers on the binding provider
			headers = new ArrayList<Header>();
			headers.add(Headers.create(otAuthElement));
			headers.add(Headers.create(contextIDElement));

			((WSBindingProvider) contentServiceClient).setOutboundHeaders(headers);
		}
		catch (SOAPException e)
		{
			LOGGER.error(e.getMessage());
			System.out.println("Failed to set SOAP headers!\n");
			System.out.println(e.getMessage());
			
		}
		catch (SOAPFaultException e)
		{
			LOGGER.error(e.getMessage());
			System.out.println("FAILED!\n");
			System.out.println(e.getFault().getFaultCode() + " : " + e.getMessage());
			
		}
			return contentServiceClient;
		
	}
	
	public static StreamingDataHandler downloadDoc(String authToken,String contextID)
	{
		// Create a StreamingDataHandler to download the file with
		StreamingDataHandler downloadStream = null;
		ContentService contentServiceClient=null;
		try 
		{
			contentServiceClient=OTUtility.getContentService(authToken, contextID);
			System.out.println("Downloading file...");
			downloadStream = (StreamingDataHandler) contentServiceClient.downloadContent(contextID);
			String contenttype=downloadStream.getContentType();
			String fileName=downloadStream.getName();
			System.out.println(contenttype+"::::"+fileName);
			//File file = new File(FILE_PATH);
			//downloadStream.moveTo(file);
			//System.out.println(contenttype +" ............"+ fileName);
			//System.out.println("Downloaded " + file.length() + " bytes to " + FILE_PATH + ".\n");
			
			
			      
		}
		
		catch (Exception e)
		{
			LOGGER.error(e.getMessage());
			System.out.println("Failed to download file!\n");
			System.out.println(e.getMessage());
		}
		return downloadStream;
	}
	
	public static List<Node> getChildren(long parentID)
	{
		DocumentManagement docManClient=null;
		String authToken=null;
		List<Node> nodes=null;
		try
		{
			authToken=OTUtility.getAuthToken();
			docManClient=OTUtility.getDocumentManagement(authToken);
			nodes=docManClient.listNodes(parentID, false);
			
			//docManClient.createFolder(parentID,"Sucker","",null);
			
		}
		catch(Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		return nodes;
			
	}
	
	public static String getNodeName(Node node)
	{
		return node.getName();
	}
	
	public static void uploaddocument(String filePath)
	{
		String authtoken=OTUtility.getAuthToken();
		OTAuthentication otAuth = null;
		DocumentManagement docManClient=OTUtility.getDocumentManagement(authtoken);
		String contextID = null;
		int PARENT_ID=85457;
		ContentService_Service contentService = null;
		ContentService contentServiceClient = null;
		File file = new File(filePath);
		BasicFileAttributes fileAttributes;
		FileAtts fileAtts = new FileAtts();

		if (!file.exists())
		{
			LOGGER.error("File does not exist at : " + filePath);
			System.out.println("ERROR!\n");
			System.out.println("File does not exist at : " + filePath);
			return;
		}
		
		try
		{
			otAuth = new OTAuthentication();
			otAuth.setAuthenticationToken(authtoken);
			
			System.out.print("Generating context ID...");
			contextID = docManClient.createDocumentContext(PARENT_ID, file.getName(), null, false, null);
			System.out.println("SUCCESS!\n");
			
			contentService = new ContentService_Service();
			contentServiceClient = contentService.getBasicHttpBindingContentService(new MTOMFeature());
			

			// The number of bytes to write in each chunk
			final int CHUNK_SIZE = 10240;
			
			// Enable streaming and use chunked transfer encoding to send the request body to support large files
			((BindingProvider) contentServiceClient).getRequestContext().put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, CHUNK_SIZE);
			fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			
			fileAtts.setCreatedDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(fileAttributes.creationTime().toString()));
			fileAtts.setFileName(file.getName());
			fileAtts.setFileSize(file.length());
			fileAtts.setModifiedDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(fileAttributes.lastModifiedTime().toString()));

			// Create a SOAP header
			SOAPHeader header = MessageFactory.newInstance().createMessage().getSOAPPart().getEnvelope().getHeader();

			// Add the OTAuthentication SOAP header element
			SOAPHeaderElement otAuthElement = header.addHeaderElement(new QName(ECM_API_NAMESPACE, "OTAuthentication"));

			// Add the AuthenticationToken
			SOAPElement authTokenElement = otAuthElement.addChildElement(new QName(ECM_API_NAMESPACE, "AuthenticationToken"));
			authTokenElement.addTextNode(otAuth.getAuthenticationToken());

			// Add the ContextID SOAP header element
			SOAPHeaderElement contextIDElement = header.addHeaderElement(new QName(CORE_NAMESPACE, "contextID"));
			contextIDElement.addTextNode(contextID);

			// Add the FileAtts SOAP header element
			SOAPHeaderElement fileAttsElement = header.addHeaderElement(new QName(CORE_NAMESPACE, "fileAtts"));

			// Add the CreatedDate element
			SOAPElement createdDateElement = fileAttsElement.addChildElement(new QName(CORE_NAMESPACE, "CreatedDate"));
			createdDateElement.addTextNode(fileAtts.getCreatedDate().toString());

			// Add the ModifiedDate element
			SOAPElement modifiedDateElement = fileAttsElement.addChildElement(new QName(CORE_NAMESPACE, "ModifiedDate"));
			modifiedDateElement.addTextNode(fileAtts.getModifiedDate().toString());

			// Add the FileSize element
			SOAPElement fileSizeElement = fileAttsElement.addChildElement(new QName(CORE_NAMESPACE, "FileSize"));
			fileSizeElement.addTextNode(fileAtts.getFileSize().toString());

			// Add the FileName element
			SOAPElement fileNameElement = fileAttsElement.addChildElement(new QName(CORE_NAMESPACE, "FileName"));
			fileNameElement.addTextNode(fileAtts.getFileName());

			// Set the headers on the binding provider
			List<Header> headers = new ArrayList<Header>();
			headers.add(Headers.create(otAuthElement));
			headers.add(Headers.create(contextIDElement));
			headers.add(Headers.create(fileAttsElement));

			((WSBindingProvider) contentServiceClient).setOutboundHeaders(headers);
			
			System.out.print("Uploading document...");
			String objectID = contentServiceClient.uploadContent(new DataHandler(new FileDataSource(file)));
			System.out.println("SUCCESS!\n");
			System.out.println("New document uploaded with ID = " + objectID);
			
			
		}
		catch (SOAPFaultException | IOException | DatatypeConfigurationException | SOAPException e)
		{
			System.out.println("FAILED!\n");
			System.out.println( " : " + e.getMessage());
			return;
		}
		
	}
	
	public static String copyDocument(int parentid,int docID,String newName)
	{
		DocumentManagement docManClient=null;
		String authToken=null;
		try
		{
			authToken=OTUtility.getAuthToken();

			docManClient=OTUtility.getDocumentManagement(authToken);
			Node n=docManClient.copyNode(docID, parentid, newName, null);
			//System.out.println(n.getID());
		}
		catch(Exception e)
		{
			
		}
		return null;
		
	}
	
	public static String moveDocument(int dataID, int parentID)
	{
		DocumentManagement docManClient=null;
		String authToken=null;
		try
		{
			System.out.println("dataid "+dataID);
			System.out.println("parentID "+parentID);
			authToken=OTUtility.getAuthToken();

			docManClient=OTUtility.getDocumentManagement(authToken);
			MoveOptions moveoptions=new MoveOptions();
			docManClient.moveNode(dataID,parentID,  null,null);
			
		}
		catch(Exception e)
		{
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		return "Moved";
	}
	
	public static String deleteDocument(int dataID)
	{
		DocumentManagement docManClient=null;
		String authToken=null;
		try
		{
			authToken=OTUtility.getAuthToken();

			docManClient=OTUtility.getDocumentManagement(authToken);
			docManClient.deleteNode(dataID);
		}
		catch(Exception e)
		{
			
		}
		return "deleted";
	}

}
