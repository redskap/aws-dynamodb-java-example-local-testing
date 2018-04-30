package io.redskap.java.aws.dynamodb.example.local.testing;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.local.shared.access.AmazonDynamoDBLocal;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleCrudTest {

  private static AmazonDynamoDBLocal amazonDynamoDBLocal;
  private SimpleCrud simpleCrud;

  @BeforeClass
  public static void setUpClass() {
    //AwsDynamoDbHelper.initSqLite();

    amazonDynamoDBLocal = DynamoDBEmbedded.create();

    // Init DB table
    new SimpleCrud(createDocumentInterfaceClient()).initDb(new ProvisionedThroughput(1L, 1L));
  }

  @AfterClass
  public static void tearDownClass() {
    amazonDynamoDBLocal.shutdown();
  }

  private static DynamoDB createDocumentInterfaceClient() {
    final AmazonDynamoDB client = amazonDynamoDBLocal.amazonDynamoDB();
    return new DynamoDB(client);
  }

  @Before
  public void setUp() {
    simpleCrud = new SimpleCrud(createDocumentInterfaceClient());
  }


  @Test
  public void when_StoreValueIsCalled_then_CanBeRetrieved() {
    // Given
    final int key = 10;
    final String value = "TEN";
    final String expected = value;

    // When
    simpleCrud.storeValue(10, value);

    // Then
    final String actual = simpleCrud.retrieveValue(key);

    Assert.assertEquals(expected, actual);
  }

  @Test(expected = IllegalStateException.class)
  public void when_RetrieveValueCalledWithNotStoredKey_then_ExceotionIsThrown() {
    // Given
    final int key = 15;

    // When
    simpleCrud.retrieveValue(key);
  }
}
