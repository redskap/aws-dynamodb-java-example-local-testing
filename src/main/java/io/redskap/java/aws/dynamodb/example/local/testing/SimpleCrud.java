package io.redskap.java.aws.dynamodb.example.local.testing;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.google.common.collect.Lists;

public class SimpleCrud {

  public static final String TABLE_NAME = "SimpleTable";

  private final DynamoDB dynamoDB;
  private final Table table;

  public SimpleCrud(final DynamoDB dynamoDB) {
    this.dynamoDB = dynamoDB;
    this.table = dynamoDB.getTable(TABLE_NAME);
  }

  public void initDb(final ProvisionedThroughput provisionedThroughput) {
    dynamoDB.createTable(new CreateTableRequest(TABLE_NAME, Lists.newArrayList(new KeySchemaElement("id", KeyType.HASH)))
            .withAttributeDefinitions(new AttributeDefinition("id", ScalarAttributeType.N))
            .withProvisionedThroughput(provisionedThroughput));
  }

  public void storeValue(final int key, final String value) {
    table.putItem(new Item().withPrimaryKey("id", key).withString("value", value));
  }

  public String retrieveValue(final int key) {
    final Item item = table.getItem("id", key);
    if (item == null) {
      throw new IllegalStateException("Value does not exist for key: " + key);
    }
    return item.getString("value");
  }
}
