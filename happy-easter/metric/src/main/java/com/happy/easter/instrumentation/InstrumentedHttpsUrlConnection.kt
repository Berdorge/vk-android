package com.happy.easter.instrumentation

import java.net.URL
import java.security.Principal
import java.security.cert.Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class InstrumentedHttpsUrlConnection(
    url: URL,
    private val delegate: HttpsURLConnection
) : InstrumentedUrlConnection by BaseInstrumentedHttpUrlConnection(url, delegate),
    HttpsURLConnection(url){
    override fun getLocalPrincipal(): Principal {
        return delegate.localPrincipal
    }

    override fun getHostnameVerifier(): HostnameVerifier {
        return delegate.hostnameVerifier
    }

    override fun getServerCertificates(): Array<Certificate> {
        return delegate.serverCertificates
    }

    override fun setHostnameVerifier(v: HostnameVerifier?) {
        delegate.hostnameVerifier = v
    }

    override fun setSSLSocketFactory(sf: SSLSocketFactory?) {
        delegate.sslSocketFactory = sf
    }

    override fun getPeerPrincipal(): Principal {
        return delegate.peerPrincipal
    }

    override fun getCipherSuite(): String {
        return delegate.cipherSuite
    }

    override fun getLocalCertificates(): Array<Certificate> {
        return delegate.localCertificates
    }

    override fun getSSLSocketFactory(): SSLSocketFactory {
        return delegate.sslSocketFactory
    }
}