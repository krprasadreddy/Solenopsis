package com.redhat.solenopsis.app;

import com.redhat.sforce.soap.metadata.DescribeMetadataObject;
import com.redhat.sforce.soap.metadata.DescribeMetadataResult;
import com.redhat.sforce.soap.metadata.FileProperties;
import com.redhat.sforce.soap.metadata.ListMetadataQuery;
import com.redhat.solenopsis.util.PackageXml;
import com.redhat.solenopsis.ws.Credentials;
import com.redhat.solenopsis.ws.LoginSvc;
import com.redhat.solenopsis.ws.MetadataSvc;
import com.redhat.solenopsis.ws.impl.DefaultEnterpriseSvc;
import com.redhat.solenopsis.ws.impl.DefaultMetadataSvc;
import com.redhat.solenopsis.ws.impl.DefaultPartnerSvc;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * The purpose of this class is
 *
 * @author sfloess
 *
 */
public class Main {
    public static void emitMetadata(final String msg, final LoginSvc loginSvc, final double apiVersion) throws Exception {
        MetadataSvc metadataSvc = new DefaultMetadataSvc(loginSvc);
        
        //metadataSvc.login();
        
        metadataSvc.getPort();
        
        final DescribeMetadataResult describeMetadata = metadataSvc.getPort().describeMetadata(apiVersion);
        
        final List<DescribeMetadataObject> metadataObjects = describeMetadata.getMetadataObjects();                
        
        for (final DescribeMetadataObject dmo : metadataObjects) {
        
            System.out.println("==============================================");
            
            System.out.println("Dir:       " + dmo.getDirectoryName());
            System.out.println("Suffix:    " + dmo.getSuffix());
            System.out.println("XML:       " + dmo.getXmlName());
            System.out.println("In folder: " + dmo.isInFolder());
            System.out.println("Meta file: " + dmo.isMetaFile());
            System.out.println("Children:");
            for (final String child : dmo.getChildXmlNames()) {
                System.out.println("          " + child);
            }                        
                    
            final ListMetadataQuery query = new ListMetadataQuery();
            query.setType(dmo.getXmlName());
            if (null != dmo.getDirectoryName() || ! "".equals(dmo.getDirectoryName())) {
                query.setFolder(dmo.getDirectoryName());
            }

            final List<ListMetadataQuery> metaDataQuertyList = new ArrayList<ListMetadataQuery>();  
            
            metaDataQuertyList.add(query);
        
            System.out.println();

            final List<FileProperties> filePropertiesList = metadataSvc.getPort().listMetadata(metaDataQuertyList, 24);
            for (final FileProperties fileProperties : filePropertiesList) {
                System.out.println ("Full name:      " + fileProperties.getFullName());
                System.out.println ("     file name: " + fileProperties.getFileName());
                System.out.println ("     type:      " + fileProperties.getType());
            }
        
            System.out.println("\n\n");
            
            System.out.println(PackageXml.computePackage(describeMetadata));
            
        }          

    }
        
    public static void main(final String[] args) throws Exception {
        //final String env = "prod.properties";
        final String env = "dev.properties";
        
        final FileInputStream fis = new FileInputStream (System.getProperty("user.home") + "/.solenopsis/credentials/" + env);
        final Properties props = new Properties();
        props.load(fis);
        
        Credentials credentials = new Credentials(props);
        
        //double apiVersion = Double.parseDouble(credentials.getApiVersion());
        
        emitMetadata("Enterprise WSDL", new DefaultEnterpriseSvc(credentials), 24);
        emitMetadata("Partner WSDL", new DefaultPartnerSvc(credentials), 24);

    }
}