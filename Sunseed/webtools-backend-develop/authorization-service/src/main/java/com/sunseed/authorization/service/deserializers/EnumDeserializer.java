package com.sunseed.authorization.service.deserializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.sunseed.authorization.service.exceptions.InvalidEnumValueException;

public class EnumDeserializer<T extends Enum<T>> extends StdDeserializer<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Class<T> enumClass;

    public EnumDeserializer(Class<T> enumClass) {
        super(enumClass);
        this.enumClass = enumClass;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getValueAsString();

        for (T enumConstant : enumClass.getEnumConstants()) {
        	
        	String enumValue = enumConstant.name().replace("_"," ");
            if (enumValue.equalsIgnoreCase(value)) {
                return enumConstant;
            }
        }

        // If the value doesn't match any enum constant, throw an exception
        throw new InvalidEnumValueException(null,enumClass.getSimpleName(),"is invalid");
    }

    @Override
    public T getNullValue(DeserializationContext ctxt) {
    	throw new InvalidEnumValueException(null,enumClass.getSimpleName(),"should not be null");
    }
}
