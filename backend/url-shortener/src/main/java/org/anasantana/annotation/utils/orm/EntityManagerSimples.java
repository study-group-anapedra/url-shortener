package org.anasantana.annotation.utils.orm;

import org.anasantana.service.exception.CodigoCurtoNaoDisponivelException;
import org.anasantana.service.exception.PersistenciaException;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

public class EntityManagerSimples {

    private final DynamoDbClient dynamoDb;
    private final String tableName;

    public EntityManagerSimples() {
        String region = System.getenv("AWS_REGION");
        if (region == null || region.isEmpty()) {
            throw new RuntimeException("AWS_REGION não definida no ambiente da Lambda");
        }

        this.dynamoDb = DynamoDbClient.builder()
                .region(Region.of(region))
                .httpClient(UrlConnectionHttpClient.builder().build())
                .build();

        this.tableName = System.getenv("TABLE_NAME");
        if (this.tableName == null || this.tableName.isEmpty()) {
            throw new RuntimeException("TABLE_NAME não definida no ambiente da Lambda");
        }
    }

    public void salvar(Object entidade) {
        Class<?> clazz = entidade.getClass();
        Map<String, AttributeValue> item = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object valor = field.get(entidade);
                if (valor != null) {
                    item.put(field.getName(), converterParaAttributeValue(valor));
                }
            } catch (IllegalAccessException e) {
                throw new PersistenciaException("Erro de Reflection ao salvar", e);
            }
        }

        if (!item.containsKey("shortCode")) {
            throw new PersistenciaException(
                    "shortCode está nulo. Não é possível salvar no DynamoDB.",
                    new NullPointerException("shortCode ausente")
            );
        }

        try {
            System.out.println("Salvando no DynamoDB: " + item);

            dynamoDb.putItem(PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .conditionExpression("attribute_not_exists(shortCode)")
                    .build());

        } catch (ConditionalCheckFailedException e) {
            throw new CodigoCurtoNaoDisponivelException();
        } catch (Exception e) {
            throw new PersistenciaException("Erro real do DynamoDB: " + e.getMessage(), e);
        }
    }

    public <T> List<T> buscarPorCampo(Class<T> clazz, String campo, Object valor, String indexName) {
        List<T> resultados = new ArrayList<>();

        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":v", converterParaAttributeValue(valor));

        Map<String, String> names = new HashMap<>();
        names.put("#campo", campo);

        QueryRequest.Builder requestBuilder = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("#campo = :v")
                .expressionAttributeValues(values)
                .expressionAttributeNames(names);

        if (indexName != null && !indexName.isEmpty()) {
            requestBuilder.indexName(indexName);
        }

        try {
            QueryResponse response = dynamoDb.query(requestBuilder.build());

            for (Map<String, AttributeValue> item : response.items()) {
                T instancia = clazz.getDeclaredConstructor().newInstance();
                for (Field f : clazz.getDeclaredFields()) {
                    f.setAccessible(true);
                    AttributeValue attr = item.get(f.getName());
                    if (attr != null) {
                        atribuirValor(instancia, f, attr);
                    }
                }
                resultados.add(instancia);
            }

        } catch (Exception e) {
            throw new PersistenciaException("Erro na consulta DynamoDB: " + e.getMessage(), e);
        }

        return resultados;
    }

    public <T> List<T> buscarPorCampo(Class<T> clazz, String campo, Object valor) {
        return buscarPorCampo(clazz, campo, valor, null);
    }

    private AttributeValue converterParaAttributeValue(Object v) {
        if (v == null) return AttributeValue.builder().nul(true).build();
        if (v instanceof String) return AttributeValue.builder().s((String) v).build();
        if (v instanceof Number) return AttributeValue.builder().n(v.toString()).build();
        if (v instanceof Boolean) return AttributeValue.builder().bool((Boolean) v).build();
        if (v instanceof UUID || v instanceof Instant) return AttributeValue.builder().s(v.toString()).build();
        return AttributeValue.builder().s(v.toString()).build();
    }

    private void atribuirValor(Object instancia, Field f, AttributeValue attr) throws IllegalAccessException {
        f.setAccessible(true);

        if (attr.s() != null) {
            String valor = attr.s();
            if (f.getType().equals(UUID.class)) {
                f.set(instancia, UUID.fromString(valor));
            } else if (f.getType().equals(Instant.class)) {
                f.set(instancia, Instant.parse(valor));
            } else {
                f.set(instancia, valor);
            }
        } 
        else if (attr.n() != null) {
            if (f.getType().equals(Long.class) || f.getType().equals(long.class)) {
                f.set(instancia, Long.valueOf(attr.n()));
            } else if (f.getType().equals(Integer.class) || f.getType().equals(int.class)) {
                f.set(instancia, Integer.valueOf(attr.n()));
            }
        } 
        else if (attr.bool() != null) {
            f.set(instancia, attr.bool());
        }
    }
}
