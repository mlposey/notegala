package com.marcusposey.notegala.net.gen;

import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.api.ResponseFieldMapper;
import com.apollographql.apollo.api.ResponseFieldMarshaller;
import com.apollographql.apollo.api.ResponseReader;
import com.apollographql.apollo.api.ResponseWriter;
import com.apollographql.apollo.api.internal.Utils;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Collections;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("Apollo GraphQL")
public final class GetAccountQuery implements Query<GetAccountQuery.Data, GetAccountQuery.Data, Operation.Variables> {
  public static final String OPERATION_DEFINITION = "query GetAccount {\n"
      + "  account {\n"
      + "    __typename\n"
      + "    email\n"
      + "    name\n"
      + "  }\n"
      + "}";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

  private static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "GetAccount";
    }
  };

  private final Operation.Variables variables;

  public GetAccountQuery() {
    this.variables = Operation.EMPTY_VARIABLES;
  }

  @Override
  public String operationId() {
    return "1a4265b18052dee923887ccf0e4b9afe8a25aabbfe73db9db50026d3ba2f4958";
  }

  @Override
  public String queryDocument() {
    return QUERY_DOCUMENT;
  }

  @Override
  public GetAccountQuery.Data wrapData(GetAccountQuery.Data data) {
    return data;
  }

  @Override
  public Operation.Variables variables() {
    return variables;
  }

  @Override
  public ResponseFieldMapper<GetAccountQuery.Data> responseFieldMapper() {
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
    Builder() {
    }

    public GetAccountQuery build() {
      return new GetAccountQuery();
    }
  }

  public static class Data implements Operation.Data {
    static final ResponseField[] $responseFields = {
      ResponseField.forObject("account", "account", null, false, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull Account account;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Data(@Nonnull Account account) {
      this.account = Utils.checkNotNull(account, "account == null");
    }

    public @Nonnull Account account() {
      return this.account;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeObject($responseFields[0], account.marshaller());
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Data{"
          + "account=" + account
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
        return this.account.equals(that.account);
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= account.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      final Account.Mapper accountFieldMapper = new Account.Mapper();

      @Override
      public Data map(ResponseReader reader) {
        final Account account = reader.readObject($responseFields[0], new ResponseReader.ObjectReader<Account>() {
          @Override
          public Account read(ResponseReader reader) {
            return accountFieldMapper.map(reader);
          }
        });
        return new Data(account);
      }
    }
  }

  public static class Account {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("email", "email", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("name", "name", null, false, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nonnull String __typename;

    final @Nonnull String email;

    final @Nonnull String name;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Account(@Nonnull String __typename, @Nonnull String email, @Nonnull String name) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.email = Utils.checkNotNull(email, "email == null");
      this.name = Utils.checkNotNull(name, "name == null");
    }

    public @Nonnull String __typename() {
      return this.__typename;
    }

    public @Nonnull String email() {
      return this.email;
    }

    public @Nonnull String name() {
      return this.name;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeString($responseFields[1], email);
          writer.writeString($responseFields[2], name);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Account{"
          + "__typename=" + __typename + ", "
          + "email=" + email + ", "
          + "name=" + name
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Account) {
        Account that = (Account) o;
        return this.__typename.equals(that.__typename)
         && this.email.equals(that.email)
         && this.name.equals(that.name);
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= __typename.hashCode();
        h *= 1000003;
        h ^= email.hashCode();
        h *= 1000003;
        h ^= name.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Account> {
      @Override
      public Account map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String email = reader.readString($responseFields[1]);
        final String name = reader.readString($responseFields[2]);
        return new Account(__typename, email, name);
      }
    }
  }
}
