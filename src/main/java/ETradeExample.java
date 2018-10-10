import com.etrade.etws.account.Account;
import com.etrade.etws.account.AccountListResponse;
import com.etrade.etws.oauth.sdk.client.IOAuthClient;
import com.etrade.etws.oauth.sdk.client.OAuthClientImpl;
import com.etrade.etws.oauth.sdk.common.Token;
import com.etrade.etws.sdk.client.AccountsClient;
import com.etrade.etws.sdk.client.ClientRequest;
import com.etrade.etws.sdk.client.Environment;
import com.etrade.etws.sdk.common.ETWSException;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;

public class ETradeExample {
    public static final String OAUTH_CONSUMER_KEY = "<OAUTH_CONSUMER_KEY>";
    public static final String OAUTH_CONSUMER_SECRET = "<OAUTH_CONSUMER_SECRET>";

    public static void main(final String[] args) throws ETWSException, IOException, URISyntaxException {
        ETradeExample provider = new ETradeExample();
        provider.getClient();
    }

    private IOAuthClient getClient() throws IOException, ETWSException, URISyntaxException {
        IOAuthClient client = OAuthClientImpl.getInstance();
        ClientRequest request = new ClientRequest();
        Token oauthRequestToken = getOauthRequestToken(client, request);
        String oauthVerifierCode = getOauthVerifierCode(client, request);
        Token oauthAccessToken = getOauthAccessToken(client, oauthRequestToken, oauthVerifierCode);
        List<Account> accounts = getAccounts(oauthVerifierCode, oauthAccessToken);

        for (Account account : accounts) {
            System.out.println("===================");
            System.out.println("Account: " + account.getAccountId());
            System.out.println("===================");
        }

        return client;
    }

    private List<Account> getAccounts(final String oauthVerifierCode, final Token oauthAccessToken)
            throws IOException, ETWSException {
        ClientRequest request = new ClientRequest();
        request.setEnv(Environment.SANDBOX);
        request.setConsumerKey(OAUTH_CONSUMER_KEY);
        request.setConsumerSecret(OAUTH_CONSUMER_SECRET);
        request.setToken(oauthAccessToken.getToken());
        request.setTokenSecret(oauthAccessToken.getSecret());
        request.setVerifierCode(oauthVerifierCode);

        AccountsClient accountClient = new AccountsClient(request);
        AccountListResponse response = accountClient.getAccountList();
        return response.getResponse();
    }

    private Token getOauthAccessToken(final IOAuthClient client, final Token oauthRequestToken, final String oauthVerifierCode)
            throws IOException, ETWSException {
        ClientRequest request = new ClientRequest();
        request.setEnv(Environment.SANDBOX);
        request.setConsumerKey(OAUTH_CONSUMER_KEY);
        request.setConsumerSecret(OAUTH_CONSUMER_SECRET);
        request.setToken(oauthRequestToken.getToken());
        request.setTokenSecret(oauthRequestToken.getSecret());
        request.setVerifierCode(oauthVerifierCode);

        return client.getAccessToken(request);
    }

    private String getOauthVerifierCode(final IOAuthClient client, final ClientRequest request)
            throws ETWSException, URISyntaxException, IOException {
        String authorizeURL = client.getAuthorizeUrl(request);
        URI uri = new URI(authorizeURL);
        Desktop.getDesktop().browse(uri);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input verifier code from browser");
        return scanner.nextLine();
    }

    private Token getOauthRequestToken(final IOAuthClient client, final ClientRequest request)
            throws IOException, ETWSException {
        request.setEnv(Environment.SANDBOX);
        request.setConsumerKey(OAUTH_CONSUMER_KEY);
        request.setConsumerSecret(OAUTH_CONSUMER_SECRET);
        Token oauthRequestToken = client.getRequestToken(request);
        request.setToken(oauthRequestToken.getToken());
        request.setTokenSecret(oauthRequestToken.getSecret());
        return oauthRequestToken;
    }
}
