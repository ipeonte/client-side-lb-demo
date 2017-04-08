package com.example.demo.cloud.rest.data_provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Spring boot launcher and processing logic
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
@EnableDiscoveryClient
@SpringBootApplication
public class DataProviderApp implements CommandLineRunner {

	// Application logger
	Logger LOG = LoggerFactory.getLogger(DataProviderApp.class);

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	RestTemplate template;

	// Trace file name;
	private File _fname;

	@Value("${demo.id}")
	private void setId(int id) {
		_fname = new File(Constants.FILE_NAME_BASE + id + ".csv");
	}

	// Map to handle statistic
	Map<String, Integer> stats = new HashMap<>();

	public static void main(String[] args) {
		SpringApplication.run(DataProviderApp.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		// Delete trace file on start up
		if (_fname.exists() && !_fname.delete())
			throw new IOException("Unable delete file " + _fname);

		// Check if any data-consumer available
		List<ServiceInstance> services = discoveryClient.getInstances(Constants.DATA_CONSUMER_NAME);
		if (services.size() == 0) {
			LOG.error("No instances of '" + Constants.DATA_CONSUMER_NAME + "' found in Eureka Server");
			System.exit(1);
		}

		LOG.info("Found " + services.size() + " instances for '" + Constants.DATA_CONSUMER_NAME + "' name");
		for (int i = 0; i < services.size(); i++)
			LOG.info("Instance #" + (i + 1) + ":" + services.get(i).getHost() + ":" + services.get(i).getPort());

		for (int i = 0; i < Constants.MSG_COUNT; i++) {
			try {
				Integer port = template.getForObject("http://" + Constants.DATA_CONSUMER_NAME + "/", Integer.class);
				LOG.debug("<-- " + port);
				record(port);

			} catch (RestClientException e) {
				LOG.error(e.getMessage());
				record("ERROR");
			}
		}

		// Save statistic at the end
		saveStatistic();
	}

	private void record(Integer port) {
		record(port.toString());
	}

	private void record(String key) {
		// Record port statistic
		Integer cnt = stats.get(key);
		stats.put(key, cnt == null ? 1 : ++cnt);
	}

	public void saveStatistic() {
		// Write port statistic into the csv file
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(_fname, true);

			// Write header
			out.write(new String("Port,Replies\n").getBytes());

			// Write info for each port
			for (Entry<String, Integer> entry : stats.entrySet())
				out.write(new String(entry.getKey() + "," + entry.getValue() + "\n").getBytes());

		} catch (IOException e) {
			LOG.error(e.getMessage());
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					// Do nothing
				}
		}
	}
}
