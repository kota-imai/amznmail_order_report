/******************************************************************************* 
 *  Copyright 2009 Amazon Services.
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  
 *  You may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at: http://aws.amazon.com/apache2.0
 *  This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 *  CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 * ***************************************************************************** 
 *
 *  Marketplace Web Service Java Library
 *  API Version: 2009-01-01
 *  Generated: Wed Feb 18 13:28:48 PST 2009 
 * 
 */



package mwssamples;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import com.amazonaws.mws.*;
import com.amazonaws.mws.model.*;

import config.SystemConfig;

/**
 *
 * Get Report Request List  Samples
 *
 *
 */
public class GetReportRequestListSample {

    /**
     * Just add a few required parameters, and try the service
     * Get Report Request List functionality
     *
     * @param args unused
     */
    public Map<String, Map<String, String>> sendRequest(List<String> idlist, String sellerId, String token) {

        /************************************************************************
         * Access Key ID and Secret Access Key ID, obtained from:
         * http://aws.amazon.com
         ***********************************************************************/
        final String accessKeyId = SystemConfig.getAwsAccessKeyId();
        final String secretAccessKey = SystemConfig.getSecretKeyId();

        final String appName = SystemConfig.getAppName();
        final String appVersion = SystemConfig.getAppVersion();

        MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();

        /************************************************************************
         * Uncomment to set the appropriate MWS endpoint.
         ************************************************************************/
        // Japan
        config.setServiceURL("https://mws.amazonservices.jp/");

        MarketplaceWebService service = new MarketplaceWebServiceClient(
                accessKeyId, secretAccessKey, appName, appVersion, config);

        GetReportRequestListRequest request = new GetReportRequestListRequest();
        request.setMerchant(sellerId);
        request.setMWSAuthToken(token);

        IdList value = new IdList();
        value.setId(idlist);
        
        request.setReportRequestIdList(value);

        return invokeGetReportRequestList(service, request);
    }

    /**
     * Get Report Request List  request sample
     * returns a list of report requests ids and their associated metadata
     *   
     * @param service instance of MarketplaceWebService service
     * @param request Action to invoke
     */
    public static Map<String, Map<String, String>> invokeGetReportRequestList(MarketplaceWebService service, GetReportRequestListRequest request) {
//    	List<Map> itemList = new ArrayList<Map>();

        Map<String, Map<String,String>> map = new HashMap<String, Map<String,String>>();
        String xmlstring = new String();
        try {
            GetReportRequestListResponse response = service.getReportRequestList(request);
            xmlstring = response.toXML();
            System.out.println();

//            System.out.println ("GetReportRequestList Action Response");
            System.out.println ("=============================================================================");
//            System.out.println ();

            System.out.print("    GetReportRequestListResponse");
            System.out.println();

            if (response.isSetGetReportRequestListResult()) {
                System.out.print("        GetReportRequestListResult");
                System.out.println();
                GetReportRequestListResult  getReportRequestListResult = response.getGetReportRequestListResult();
                if (getReportRequestListResult.isSetNextToken()) {
                    System.out.print("            NextToken");
                    System.out.println();
                    System.out.print("                " + getReportRequestListResult.getNextToken());
                    System.out.println();
                }
                if (getReportRequestListResult.isSetHasNext()) {
                    System.out.print("            HasNext");
                    System.out.println();
                    System.out.print("                " + getReportRequestListResult.isHasNext());
                    System.out.println();
                }
                java.util.List<ReportRequestInfo> reportRequestInfoList = getReportRequestListResult.getReportRequestInfoList();
//                Map item = new HashMap();
                for (ReportRequestInfo reportRequestInfo : reportRequestInfoList) {
                    System.out.print("            ReportRequestInfo");
                    System.out.println();
//                    String reportRequestId = null;
                    if (reportRequestInfo.isSetReportRequestId()) {
                        System.out.print("                ReportRequestId");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getReportRequestId());
                        System.out.println();
                        Map<String, String> submap = new HashMap<String, String>();
                        map.put(reportRequestInfo.getReportRequestId(), submap);
                    }
//                    if (reportRequestInfo.isSetReportType()) {
//                        System.out.print("                ReportType");
//                        System.out.println();
//                        System.out.print("                    " + reportRequestInfo.getReportType());
//                        System.out.println();
//                    }
                    if (reportRequestInfo.isSetStartDate()) {
                        System.out.print("                StartDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getStartDate());
                        System.out.println();
                        map.get(reportRequestInfo.getReportRequestId()).put("StartDate", reportRequestInfo.getStartDate().toString());
                    }
                    if (reportRequestInfo.isSetEndDate()) {
                        System.out.print("                EndDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getEndDate());
                        System.out.println();
                        map.get(reportRequestInfo.getReportRequestId()).put("EndDate", reportRequestInfo.getEndDate().toString());
                    }
                    if (reportRequestInfo.isSetSubmittedDate()) {
                        System.out.print("                SubmittedDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getSubmittedDate());
                        System.out.println();
                        map.get(reportRequestInfo.getReportRequestId()).put("SubmittedDate", reportRequestInfo.getSubmittedDate().toString());
                    }
                    if (reportRequestInfo.isSetCompletedDate()) {
                        System.out.print("                CompletedDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getCompletedDate());
                        System.out.println();
                        map.get(reportRequestInfo.getReportRequestId()).put("CompletedDate", reportRequestInfo.getCompletedDate().toString());
                    }                    
                    if (reportRequestInfo.isSetReportProcessingStatus()) {
                        System.out.print("                ReportProcessingStatus");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getReportProcessingStatus());
                        System.out.println();
                        map.get(reportRequestInfo.getReportRequestId()).put("ReportProcessingStatus", reportRequestInfo.getReportProcessingStatus().toString());
                    }
                    if (reportRequestInfo.isSetGeneratedReportId()) {
                        System.out.print("                GeneratedReportId");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getGeneratedReportId());
                        System.out.println();
                        map.get(reportRequestInfo.getReportRequestId()).put("GeneratedReportId", reportRequestInfo.getGeneratedReportId().toString());
                    }
                }
            } 
            if (response.isSetResponseMetadata()) {
                System.out.print("        ResponseMetadata");
                System.out.println();
                ResponseMetadata  responseMetadata = response.getResponseMetadata();
                if (responseMetadata.isSetRequestId()) {
                    System.out.print("            RequestId");
                    System.out.println();
                    System.out.print("                " + responseMetadata.getRequestId());
                    System.out.println();
                }
            } 
            System.out.println();
            System.out.println(response.getResponseHeaderMetadata());
            System.out.println();

        } catch (MarketplaceWebServiceException ex) {
        	xmlstring = ex.getXML();
            System.out.println("Caught Exception: " + ex.getMessage());
            System.out.println("Response Status Code: " + ex.getStatusCode());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Error Type: " + ex.getErrorType());
            System.out.println("Request ID: " + ex.getRequestId());
            System.out.print("XML: " + xmlstring);
            System.out.println("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata());
        }
        return map;
    }

}
