/**
 * 
 */
package za.co.sindi.jsonweb.jose.jws.impl;

import java.security.Key;

import za.co.sindi.codec.Strings;
import za.co.sindi.codec.exception.EncodingException;
import za.co.sindi.common.utils.PreConditions;
import za.co.sindi.jsonweb.jose.jws.JWSException;
import za.co.sindi.jsonweb.jose.jws.JWSJOSEHeader;
import za.co.sindi.jsonweb.jose.jws.JWSJSONSerialization;
import za.co.sindi.jsonweb.jose.jws.JWSPayload;
import za.co.sindi.jsonweb.jose.jws.JWSUtils;
import za.co.sindi.jsonweb.json.JSONArrayBuilder;
import za.co.sindi.jsonweb.json.JSONBuilderFactory;
import za.co.sindi.jsonweb.json.JSONObject;
import za.co.sindi.jsonweb.json.JSONObjectBuilder;
import za.co.sindi.jsonweb.json.impl.DefaultJSONBuilderFactory;;

/**
 * @author Bienfait Sindi
 * @since 05 July 2017
 *
 */
public class GeneralJWSJSONSerialization extends JWSJSONSerialization {

	private JSONBuilderFactory jsonBuilderFactory;
	private JSONArrayBuilder signaturesArrayBuilder;
	
	/**
	 * @param jwsPayload
	 */
	public GeneralJWSJSONSerialization(JWSPayload jwsPayload) {
		this(jwsPayload, new DefaultJSONBuilderFactory());
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param jwsPayload
	 * @param jsonBuilderFactory
	 */
	public GeneralJWSJSONSerialization(JWSPayload jwsPayload, JSONBuilderFactory jsonBuilderFactory) {
		super(jwsPayload);
		PreConditions.checkArgument(jsonBuilderFactory != null, "No JSONBuilderFactory was provided.");
		this.jsonBuilderFactory = jsonBuilderFactory;
		signaturesArrayBuilder = jsonBuilderFactory.createJSONArrayBuilder();
	}

	public GeneralJWSJSONSerialization addSignature(final JWSJOSEHeader protectedJwsHeader, final JWSJOSEHeader unprotectedJwsHeader, final Key key) throws JWSException {
		PreConditions.checkArgument(protectedJwsHeader != null || unprotectedJwsHeader != null, "A JWS Protected or Unprotected Header is required.");
		PreConditions.checkArgument(key != null, "No cryptographic key was specified.");
		
		try {
			JSONObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createJSONObjectBuilder();
			if (protectedJwsHeader != null) {
				jsonObjectBuilder.add("protected", Strings.asASCIIString(JWSUtils.encodeJwsJoseHeader(protectedJwsHeader)));
			}
			
			if (unprotectedJwsHeader != null) {
				jsonObjectBuilder.add("header", unprotectedJwsHeader.toString());
			}
			
			jsonObjectBuilder.add("signature", Strings.asASCIIString(generateJwsSignature(protectedJwsHeader, protectedJwsHeader, key)));
			
			signaturesArrayBuilder.add(jsonObjectBuilder);
		} catch (EncodingException e) {
			// TODO Auto-generated catch block
			throw new JWSException(e);
		}
		
		return this;
	}

	/* (non-Javadoc)
	 * @see za.co.sindi.jsonweb.jose.jws.JWSJSONSerialization#serialize()
	 */
	@Override
	public JSONObject serialize() throws JWSException {
		// TODO Auto-generated method stub
		JSONObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createJSONObjectBuilder();
		try {
			if (!payloadDetached) {
				jsonObjectBuilder.add("payload", Strings.asASCIIString(JWSUtils.encodeJwsPayload(jwsPayload)));
			}
			jsonObjectBuilder.add("signatures", signaturesArrayBuilder);
		} catch (EncodingException e) {
			// TODO Auto-generated catch block
			throw new JWSException(e);
		}
		
		return jsonObjectBuilder.build();
	}
}
