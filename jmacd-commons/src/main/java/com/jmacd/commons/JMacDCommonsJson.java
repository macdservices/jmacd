package com.jmacd.commons;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public interface JMacDCommonsJson {

	default String getPrettyJson(Object object) {
		return toPrettyJson(object);
	}

	public static Type ExposedMethodsListType = new TypeToken<List<ExposedMethods>>() {
	}.getType();

	default String toJson(Object object, Type typeOfSrc) {
		return new GsonBuilder().serializeNulls()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(ExposedMethods.class, new MethodSerializer()).create().toJson(object, typeOfSrc);
	}

	default String toJson(Object object) {
		if (object instanceof List<?>) {
			return toJson(object, ExposedMethodsListType);
		}

		return new GsonBuilder().serializeNulls()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(ExposedMethods.class, new MethodSerializer()).create().toJson(object);
	}

	default String toPrettyJson(Object object, Type typeOfSrc) {
		return new GsonBuilder().serializeNulls()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(ExposedMethods.class, new MethodSerializer()).setPrettyPrinting().create()
				.toJson(object, typeOfSrc);
	}

	default String toPrettyJson(Object object) {
		return new GsonBuilder().serializeNulls()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(ExposedMethods.class, new MethodSerializer())//
				.setPrettyPrinting()//
				.create().toJson(object);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	// can use in method only.
	public static @interface ExposeMethod {
	};

	public static interface ExposedMethods {
	};

	public static class MethodSerializer implements JsonSerializer<Object> {
		@Override
		public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
			Gson gson = new Gson();
			JsonObject tree = (JsonObject) gson.toJsonTree(src);

			try {
				PropertyDescriptor[] properties = Introspector.getBeanInfo(src.getClass()).getPropertyDescriptors();
				for (PropertyDescriptor property : properties) {
					if (property.getReadMethod().getAnnotation(ExposeMethod.class) != null) {
						Object result = property.getReadMethod().invoke(src, (Object[]) null);

						tree.add(property.getName(), gson.toJsonTree(result));
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return tree;
		}
	}

	public static class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
		private final DateFormat dateFormatWithZ;
		private final DateFormat dateFormat;

		// "2016-07-27T15:30:40.864Z"
		private DateTypeAdapter() {
			dateFormatWithZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US); //$NON-NLS-1$
			dateFormatWithZ.setTimeZone(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$

			dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US); //$NON-NLS-1$
		}

		@Override
		public synchronized JsonElement serialize(Date date, Type type,
				JsonSerializationContext jsonSerializationContext) {
			return new JsonPrimitive(dateFormat.format(date));
		}

		@Override
		public synchronized Date deserialize(JsonElement jsonElement, //
				Type type, //
				JsonDeserializationContext jsonDeserializationContext) {
			try {
				String jsonString = jsonElement.getAsString();
				if (jsonString.endsWith("Z") == true) { //$NON-NLS-1$
					return dateFormatWithZ.parse(jsonString);
				} else {
					return dateFormat.parse(jsonString);
				}
			} catch (ParseException e) {
				throw new JsonParseException(e);
			}
		}
	}

	public static Object fromJsonWithGitLabDateFormat(String jsonString, Class<?> aClass) {
		return new GsonBuilder()//
				.serializeNulls()//
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)//
				.registerTypeAdapter(ExposedMethods.class, new MethodSerializer())//
				.registerTypeAdapter(Date.class, new DateTypeAdapter())//
				.registerTypeAdapter(java.sql.Date.class, new DateTypeAdapter())//
				.create()//
				.fromJson(jsonString, aClass);
	}

	default <T> T fromJson(String jsonString, Class<T> aClass) {
		return new GsonBuilder()//
				.serializeNulls()//
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)//
				.registerTypeAdapter(ExposedMethods.class, new MethodSerializer())//
				.create()//
				.fromJson(jsonString, aClass);
	}

	default GsonBuilder fromJsonBuilder() {
		return new GsonBuilder()//
				.serializeNulls()//
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)//
				.registerTypeAdapter(ExposedMethods.class, new MethodSerializer());
	}

	public static class EncryptionSerializer implements JsonSerializer<EncryptedString> {

		private final PropertyEncryptionProvider propertyEncryptionProvider;

		public EncryptionSerializer(PropertyEncryptionProvider propertyEncryptionProvider) {
			super();

			this.propertyEncryptionProvider = propertyEncryptionProvider;
		}

		@Override
		public JsonElement serialize(EncryptedString encryptedString, Type typeOfSrc,
				JsonSerializationContext context) {
			final JsonObject jsonObject = new JsonObject();

			if (propertyEncryptionProvider == null) {
				jsonObject.addProperty("encryptedValue", (String) null); //$NON-NLS-1$
			} else {
				jsonObject.addProperty("encryptedValue", //$NON-NLS-1$
						propertyEncryptionProvider.encryptProperty(encryptedString.getValue()));
			}

			return jsonObject;
		}
	}

	public static class DecryptionDeserializer implements JsonDeserializer<EncryptedString> {

		private final PropertyEncryptionProvider propertyEncryptionProvider;

		public DecryptionDeserializer(PropertyEncryptionProvider propertyEncryptionProvider) {
			super();

			this.propertyEncryptionProvider = propertyEncryptionProvider;
		}

		@Override
		public EncryptedString deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			String value = ""; //$NON-NLS-1$

			try {
				final JsonObject jsonObject = json.getAsJsonObject();

				final JsonElement jsonEncryptedValue = jsonObject.get("encryptedValue"); //$NON-NLS-1$

				if (jsonEncryptedValue.isJsonNull() == true) {
					value = ""; //$NON-NLS-1$
				} else {
					value = propertyEncryptionProvider.decryptProperty(jsonEncryptedValue.getAsString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return new EncryptedString(value);
		}
	}

	default Object fromJson(String jsonString, Class<?> aClass, PropertyEncryptionProvider propertyEncryptionProvider) {
		return new GsonBuilder().serializeNulls()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(ExposedMethods.class, new MethodSerializer())
				.registerTypeAdapter(EncryptedString.class, new DecryptionDeserializer(propertyEncryptionProvider))
				.disableHtmlEscaping().create().fromJson(jsonString, aClass);
	}

	default String toPrettyJson(Object object, PropertyEncryptionProvider propertyEncryptionProvider) {
		if (object instanceof List<?>) {
			throw new RuntimeException("Not supported yet..."); //$NON-NLS-1$
//			throw new RedRuntimeException(RedCommonsProject.JSON_ERROR_1, //
//					"Not supported yet..."); //$NON-NLS-1$
			// return toPrettyJson(object, ExposedMethodsListType);
		}

		return new GsonBuilder().serializeNulls()
				// .disableHtmlEscaping()
				.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(ExposedMethods.class, new MethodSerializer())
				.registerTypeAdapter(EncryptedString.class, new EncryptionSerializer(propertyEncryptionProvider))
				.disableHtmlEscaping().setPrettyPrinting().create().toJson(object);
	}

}
