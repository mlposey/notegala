package com.marcusposey.notegala.net.gen.mutation;

import com.apollographql.apollo.api.InputFieldMarshaller;
import com.apollographql.apollo.api.InputFieldWriter;
import com.apollographql.apollo.api.Mutation;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.api.ResponseFieldMapper;
import com.apollographql.apollo.api.ResponseFieldMarshaller;
import com.apollographql.apollo.api.ResponseReader;
import com.apollographql.apollo.api.ResponseWriter;
import com.apollographql.apollo.api.internal.UnmodifiableMapBuilder;
import com.apollographql.apollo.api.internal.Utils;
import java.io.IOException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("Apollo GraphQL")
public final class RemoveNotebookMutation implements Mutation<RemoveNotebookMutation.Data, RemoveNotebookMutation.Data, RemoveNotebookMutation.Variables> {
  public static final String OPERATION_DEFINITION = "mutation RemoveNotebook($id: ID!) {\n"
      + "  wasRemoved: removeNotebook(id: $id)\n"
      + "}";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

  private static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "RemoveNotebook";
    }
  };

  private final RemoveNotebookMutation.Variables variables;

  public RemoveNotebookMutation(@Nonnull String id) {
    Utils.checkNotNull(id, "id == null");
    variables = new RemoveNotebookMutation.Variables(id);
  }

  @Override
  public String operationId() {
    return "98a05ede19f90d1abedce48f677335240f234985cbbbf2d7e82ad74e3d120a84";
  }

  @Override
  public String queryDocument() {
    return QUERY_DOCUMENT;
  }

  @Override
  public RemoveNotebookMutation.Data wrapData(RemoveNotebookMutation.Data data) {
    return data;
  }

  @Override
  public RemoveNotebookMutation.Variables variables() {
    return variables;
  }

  @Override
  public ResponseFieldMapper<RemoveNotebookMutation.Data> responseFieldMapper() {
    return new Data.Mapper();
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public OperationName name() {
    return OPERATION_NAME;
  }

  public static final class Builder {
    private @Nonnull String id;

    Builder() {
    }

    public Builder id(@Nonnull String id) {
      this.id = id;
      return this;
    }

    public RemoveNotebookMutation build() {
      Utils.checkNotNull(id, "id == null");
      return new RemoveNotebookMutation(id);
    }
  }

  public static final class Variables extends Operation.Variables {
    private final @Nonnull String id;

    private final transient Map<String, Object> valueMap = new LinkedHashMap<>();

    Variables(@Nonnull String id) {
      this.id = id;
      this.valueMap.put("id", id);
    }

    public @Nonnull String id() {
      return id;
    }

    @Override
    public Map<String, Object> valueMap() {
      return Collections.unmodifiableMap(valueMap);
    }

    @Override
    public InputFieldMarshaller marshaller() {
      return new InputFieldMarshaller() {
        @Override
        public void marshal(InputFieldWriter writer) throws IOException {
          writer.writeCustom("id", com.marcusposey.notegala.type.CustomType.ID, id);
        }
      };
    }
  }

  public static class Data implements Operation.Data {
    static final ResponseField[] $responseFields = {
      ResponseField.forBoolean("wasRemoved", "removeNotebook", new UnmodifiableMapBuilder<String, Object>(1)
        .put("id", new UnmodifiableMapBuilder<String, Object>(2)
          .put("kind", "Variable")
          .put("variableName", "id")
        .build())
      .build(), false, Collections.<ResponseField.Condition>emptyList())
    };

    final boolean wasRemoved;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Data(boolean wasRemoved) {
      this.wasRemoved = wasRemoved;
    }

    public boolean wasRemoved() {
      return this.wasRemoved;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeBoolean($responseFields[0], wasRemoved);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Data{"
          + "wasRemoved=" + wasRemoved
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Data) {
        Data that = (Data) o;
        return this.wasRemoved == that.wasRemoved;
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= Boolean.valueOf(wasRemoved).hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      @Override
      public Data map(ResponseReader reader) {
        final boolean wasRemoved = reader.readBoolean($responseFields[0]);
        return new Data(wasRemoved);
      }
    }
  }
}
