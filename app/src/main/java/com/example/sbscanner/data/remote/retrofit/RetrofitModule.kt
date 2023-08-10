package com.example.sbscanner.data.remote.retrofit

import com.example.sbscanner.data.local.files.UrlOption
import com.example.sbscanner.data.remote.retrofit.api.BoxApi
import com.example.sbscanner.data.remote.retrofit.api.DocumentApi
import com.example.sbscanner.data.remote.retrofit.api.ImageApi
import com.example.sbscanner.data.remote.retrofit.api.TaskApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class RetrofitModule(option: UrlOption) {

    private var retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .client(createOkHttpClient())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("${option.baseUrl}:${option.port}/")
            .build()
    }

    private fun createOkHttpClient() = getUnsafeOkHttpClient()
        .addInterceptor(createLoggingInterceptor())
        .build()

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                        return arrayOf()
                    }
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory
            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> =
                trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + trustManagers.contentToString()
            }

            val trustManager =
                trustManagers[0] as X509TrustManager

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun createLoggingInterceptor() = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    val taskApi: TaskApi = retrofit.create(TaskApi::class.java)

    val boxApi: BoxApi = retrofit.create(BoxApi::class.java)

    val documentApi: DocumentApi = retrofit.create(DocumentApi::class.java)

    val imageApi: ImageApi = retrofit.create(ImageApi::class.java)

    companion object {
        private const val BASE_URL = "https://procn.archiv.ru:4433/"

    }
}
