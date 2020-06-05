package dataaccess.parents;

import com.amazonaws.AmazonClientException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import config.SystemConfig;

// DynamoDbDao抽象クラス
// 初期化式を定義
public class DynamoDbDao {

	protected static AmazonDynamoDB dynamoDB;
	
	private String tableName;

	// 初期化用
	protected static void init() throws Exception {

		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			// Credentialを設定する
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (C:\\Users\\kima0\\.aws\\credentials), and is in valid format.", e);
		}
		dynamoDB = AmazonDynamoDBClientBuilder.standard().withCredentials(credentialsProvider)
				.withRegion(SystemConfig.getRegionProd()).build();
	}

	// 例外ログ出力用
	protected void LoggerAmazonServiceException(AmazonServiceException ase) {
		System.out.println("Caught an AmazonServiceException, which means your request made it "
				+ "to AWS, but was rejected with an error response for some reason.");
		System.out.println("Error Message:    " + ase.getMessage());
		System.out.println("HTTP Status Code: " + ase.getStatusCode());
		System.out.println("AWS Error Code:   " + ase.getErrorCode());
		System.out.println("Error Type:       " + ase.getErrorType());
		System.out.println("Request ID:       " + ase.getRequestId());
	}

	protected void LoggerAmazonClientException(AmazonClientException ace) {
		System.out.println("Caught an AmazonClientException, which means the client encountered "
				+ "a serious internal problem while trying to communicate with AWS, "
				+ "such as not being able to access the network.");
		System.out.println("Error Message: " + ace.getMessage());
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}