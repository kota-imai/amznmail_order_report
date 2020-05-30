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

import java.util.Arrays;
import java.util.Calendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.amazonaws.mws.*;
import com.amazonaws.mws.model.*;

import config.SystemConfig;
import dataaccess.RequestReportDao;
import util.UtilityTools;

/**
 * Request Report  Samples
 */
public class RequestReportSample {
    String merchantId;
    String sellerDevAuthToken;
    
    public void sendRequest2h(String sellerId, String token) {
        
        /************************************************************************
         * Access Key ID and Secret Access Key ID, obtained from:
         * http://aws.amazon.com
         ***********************************************************************/
        final String accessKeyId = SystemConfig.getAwsAccessKeyId();
        final String secretAccessKey = SystemConfig.getSecretKeyId();
        final String appName = SystemConfig.getAppName();
        final String appVersion = SystemConfig.getAppVersion();
        
        this.merchantId = sellerId;
        this.sellerDevAuthToken = token;

        MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();

        /************************************************************************
         * Uncomment to set the appropriate MWS endpoint.
         ************************************************************************/
        // Japan
        config.setServiceURL("https://mws.amazonservices.jp/");

        /************************************************************************
         * Instantiate Http Client Implementation of Marketplace Web Service        
         ***********************************************************************/
        
        MarketplaceWebService service = new MarketplaceWebServiceClient(
                    accessKeyId, secretAccessKey, appName, appVersion, config);
        final IdList marketplaces = new IdList(Arrays.asList(
        		"A1VC38T7YXB528"));//amazon.co.jp
        
        RequestReportRequest request = new RequestReportRequest()
		        .withMerchant(merchantId)
		        .withMarketplaceIdList(marketplaces)
		        .withReportType("_GET_AMAZON_FULFILLED_SHIPMENTS_DATA_");
//		        .withReportOptions("");
        //request = request.withMWSAuthToken(sellerDevAuthToken);
        UtilityTools util = new UtilityTools();
        
        //end	-0H前
        //start	-2H前
        try {
        	//set endDate
            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.HOUR_OF_DAY, -9);// Time difference Considered (UTC)
            cal.add(Calendar.HOUR_OF_DAY, -0);// end -0H
            
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH)+1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
//            int minute = cal.get(Calendar.MINUTE);
            int minute = 0;
            XMLGregorianCalendar endDate = util.getXMLGregCalendar(year, month, day, hour, minute);
            endDate.setTimezone(540);//+09:00JST
            request.setEndDate(endDate);
            System.out.println("EndDate : " + util.calendar2StrFormatter(endDate));
		
            //set startDate
			DatatypeFactory df = DatatypeFactory.newInstance();
			XMLGregorianCalendar startDate = (XMLGregorianCalendar) endDate.clone();
			startDate.add(df.newDuration("-PT2H"));// calc enddate -2H
            request.setStartDate(startDate);
            System.out.println("StartDate : " + util.calendar2StrFormatter(startDate));
        } catch(Exception e) {
        	e.printStackTrace();
        }
		invokeRequestReport(service, request);
    }

    /**
     * Request Report  request sample
     * requests the generation of a report
     *   
     * @param service instance of MarketplaceWebService service
     * @param request Action to invoke
     */
    public void invokeRequestReport(MarketplaceWebService service, RequestReportRequest request) {
        try {
            
            RequestReportResponse response = service.requestReport(request);
            UtilityTools util = new UtilityTools();
            
//            System.out.println ("RequestReport Action Response");
            System.out.println ("=============================================================================");
//            System.out.println ();

            System.out.print("    RequestReportResponse");
            System.out.println();
            if (response.isSetRequestReportResult()) {
                System.out.print("        RequestReportResult");
                System.out.println();
                RequestReportResult  requestReportResult = response.getRequestReportResult();
                if (requestReportResult.isSetReportRequestInfo()) {
                    System.out.print("            ReportRequestInfo");
                    System.out.println();
                    ReportRequestInfo  reportRequestInfo = requestReportResult.getReportRequestInfo();
                    if (reportRequestInfo.isSetReportRequestId()) {
                        System.out.print("                ReportRequestId");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getReportRequestId());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetReportType()) {
                        System.out.print("                ReportType");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getReportType());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetStartDate()) {
                        System.out.print("                StartDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getStartDate());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetEndDate()) {
                        System.out.print("                EndDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getEndDate());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetSubmittedDate()) {
                        System.out.print("                SubmittedDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getSubmittedDate());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetReportProcessingStatus()) {
                        System.out.print("                ReportProcessingStatus");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getReportProcessingStatus());
                        System.out.println();
                    }
                    
                    try {
                    	RequestReportDao dao = new RequestReportDao();
                        dao.saveReportId(reportRequestInfo.getReportRequestId(), merchantId, util.calendar2StrFormatter(reportRequestInfo.getStartDate()), util.calendar2StrFormatter(reportRequestInfo.getEndDate()), util.calendar2StrFormatter(reportRequestInfo.getSubmittedDate()));
                    } catch (Exception e) {
                    	e.printStackTrace();
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
            
            System.out.println("Caught Exception: " + ex.getMessage());
            System.out.println("Response Status Code: " + ex.getStatusCode());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Error Type: " + ex.getErrorType());
            System.out.println("Request ID: " + ex.getRequestId());
            System.out.print("XML: " + ex.getXML());
            System.out.println("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata());
        }
    }                                               
}
