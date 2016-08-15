package com.diamondq.common.security.acme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.shredzone.acme4j.util.CertificateUtils;
import org.shredzone.acme4j.util.KeyPairUtils;

public class WriteToStore {

	public static void main(String[] args) throws Throwable {
		File domainKey = new File("domain.key");
		File domainCert = new File("domain_cert.file");
		File certChain = new File("cert_chain.file");
		File store = new File("keystore.jks");

		KeyPair domainKeyPair;
		try (FileReader fr = new FileReader(domainKey)) {
			domainKeyPair = KeyPairUtils.readKeyPair(fr);
		}

		List<Certificate> chainList = new ArrayList<>();
		try (InputStream is = new FileInputStream(domainCert)) {
			X509Certificate certificate = CertificateUtils.readX509Certificate(is);
			chainList.add(certificate);
		}
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		try (InputStream is = new FileInputStream(certChain)) {
			Certificate certificate = certificateFactory.generateCertificate(is);
			chainList.add(certificate);
		}

		Certificate[] chain = chainList.toArray(new Certificate[0]);
		KeyStore instance = KeyStore.getInstance("JKS");
		instance.load(null);
		instance.setKeyEntry("cert", domainKeyPair.getPrivate(), "secret".toCharArray(), chain);

		try (OutputStream os = new FileOutputStream(store)) {
			instance.store(os, "secret".toCharArray());
		}
	}
}
