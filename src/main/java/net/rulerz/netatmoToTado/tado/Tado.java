package net.rulerz.netatmoToTado.tado;

import com.google.gson.Gson;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.json.JSONException;

public class Tado {
    // API URLs that will be used for requests, see:
    // https://dev.netatmo.com/doc.
    private static final String URL_AUTH_BASE = "https://auth.tado.com";
    private static final String URL_API_BASE = "https://my.tado.com";
    private static final String URL_REQUEST_TOKEN = URL_AUTH_BASE + "/oauth/token";
    private static final String URL_REQUEST_STATE = URL_API_BASE + "/api/v2/homes/614943/zones/6/state";
    private static final String URL_REQUEST_OFFSET = URL_API_BASE + "/api/v2/devices/VA3057589504/temperatureOffset";

    private final OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
    private OAuthJSONAccessTokenResponse token;

    public void login(final String email, final String password) throws TadoOAuthException {

        try {
            OAuthClientRequest request = OAuthClientRequest.tokenLocation(URL_REQUEST_TOKEN)
                    .setGrantType(GrantType.PASSWORD)
                    .setUsername(email)
                    .setPassword(password)
                    .setClientId("tado-web-app")
                    .setClientSecret("wZaRN7rpjn3FoNyF5IFuxg9uMzYJcvOoQ8QWiIqS3hfk6gLhVlG57j5YNoZL2Rtc")
                    .buildBodyMessage();

            token = oAuthClient.accessToken(request);
        } catch (OAuthSystemException | OAuthProblemException e) {
            throw new TadoOAuthException(e);
        }
    }

    public TadoState getStateData() throws TadoOAuthException, TadoParseException {
        try {
            final OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(URL_REQUEST_STATE)
                    .setAccessToken(token.getAccessToken())
                    .buildQueryMessage();
            final OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
            System.out.println("GetStationData: " + resourceResponse.getResponseCode());
            System.out.println(resourceResponse.getBody());
            System.out.println(" ");
            Gson gson = new Gson();
            return  gson.fromJson(resourceResponse.getBody(), TadoState.class);
        } catch (OAuthSystemException | OAuthProblemException e) {
            throw new TadoOAuthException(e);
        } catch (JSONException e) {
            throw new TadoParseException(e);
        }
    }

    public TadoOffset getOffsetData() throws TadoOAuthException, TadoParseException {
        try {
            final OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(URL_REQUEST_OFFSET)
                    .setAccessToken(token.getAccessToken())
                    .buildQueryMessage();
            final OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
            System.out.println("GetOffsetData: " + resourceResponse.getResponseCode());
            System.out.println(resourceResponse.getBody());
            System.out.println(" ");
            Gson gson = new Gson();
            return  gson.fromJson(resourceResponse.getBody(), TadoOffset.class);
        } catch (OAuthSystemException | OAuthProblemException e) {
            throw new TadoOAuthException(e);
        } catch (JSONException e) {
            throw new TadoParseException(e);
        }
    }

    public void setOffsetData(TadoOffset tadoOffset) {
        Gson gson = new Gson();
        String jsonToSend = gson.toJson(tadoOffset);
        try {
            final OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(URL_REQUEST_OFFSET)
                    .setAccessToken(token.getAccessToken())
                    .buildQueryMessage();
            bearerClientRequest.setBody(jsonToSend);
            final OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.PUT, OAuthResourceResponse.class);
            System.out.println("SetOffsetData: " + resourceResponse.getResponseCode());
            System.out.println(jsonToSend);
            System.out.println(" ");
        } catch (OAuthSystemException | OAuthProblemException e) {
            throw new TadoOAuthException(e);
        } catch (JSONException e) {
            throw new TadoParseException(e);
        }
    }
}
