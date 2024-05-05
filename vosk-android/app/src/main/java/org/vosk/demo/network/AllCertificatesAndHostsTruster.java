package org.vosk.demo.network;

import android.util.Log;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * TrustManager that accepts all certificates and hosts.
 * Useful when you want to use HTTPS but you have self-signed certificates.
 * Works with HttpsUrlConnection.
 * Use at your own risk and only for development.
 *
 * @author gotev (Aleksandar Gotev)
 */
public class AllCertificatesAndHostsTruster implements TrustManager, X509TrustManager {

    @Override
    public final void checkClientTrusted(final X509Certificate[] xcs, final String string)
            throws CertificateException {
    }

    @Override
    public final void checkServerTrusted(final X509Certificate[] xcs, final String string)
            throws CertificateException {
    }

    @Override
    public final X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    /**
     * Gets an {@link SSLContext} which trusts all certificates.
     * @return {@link SSLContext}
     */
    public static SSLContext getSSLContext() {
        final TrustManager[] trustAllCerts =
                new TrustManager[] {new AllCertificatesAndHostsTruster()};

        try {
            final SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustAllCerts, new SecureRandom());
            return context;

        } catch (Exception exc) {
            Log.e("CertHostTruster", "Unable to initialize the Trust Manager to trust all the "
                    + "SSL certificates and HTTPS hosts.", exc);
            return null;
        }
    }

    /**
     * Creates an hostname verifier which accepts all hosts.
     * @return {@link HostnameVerifier}
     */
    public static HostnameVerifier getAllHostnamesVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    /**
     * Call this method once before all your network calls
     * to accept all the self-signed certificates in HTTPS connections.
     */
    public static void apply() {
        final TrustManager[] trustAllCerts =
                new TrustManager[] {new AllCertificatesAndHostsTruster()};

        try {
            final SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

        } catch (Exception exc) {
            Log.e("CertHostTruster", "Unable to initialize the Trust Manager to trust all the "
                    + "SSL certificates and HTTPS hosts.", exc);
        }
    }
}