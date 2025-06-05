package com.sunseed.simtool.bootup;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.sunseed.simtool.entity.E2EMachineNode;
import com.sunseed.simtool.repository.E2EMachineNodeRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StartupCleaner implements ApplicationRunner {

	private final AppStartupState appStartupState;
	private final E2EMachineNodeRepository e2eMachineNodeRepository;

	@Value("${ssh.key.path}")
	private String sshKeyPath;

	public StartupCleaner(AppStartupState appStartupState, E2EMachineNodeRepository e2eMachineNodeRepository) {
		this.appStartupState = appStartupState;
		this.e2eMachineNodeRepository = e2eMachineNodeRepository;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("Performing startup cleanup...");
		try {
//            List<E2EMachineNode> allNodes = e2eMachineNodeRepository.findAllRunningNodes();
//            for (E2EMachineNode node : allNodes) {
//                try {
//                    killSimulationProcessesOnNode(node);
//                    log.info("Killed simulations on node: " + node.getPublic_ip_address());
//                } catch (Exception e) {
//                    log.error("Failed to clean node " + node.getPublic_ip_address() + ": " + e.getMessage());
//                }
//            }

			log.info("Reset all running node loads to 0");

		}
//            catch (Exception e) {
//            log.error("Startup cleanup failed: " + e.getMessage(), e);
//        } 
		finally {
			e2eMachineNodeRepository.resetAllRunningNodeLoadToZero(BigDecimal.ZERO);

			appStartupState.markInitialized();
			log.info("Application is ready");
		}
	}

	private void killSimulationProcessesOnNode(E2EMachineNode node) throws Exception {
		String command = "pkill -f 'simulation|Xvfb'";
		String user = node.getUsername();
		String host = node.getPublic_ip_address(); // You can adjust this based on where/how you store the key

		JSch jsch = new JSch();
		jsch.addIdentity(sshKeyPath);

		Session session = null;
		ChannelExec channel = null;

		try {
			session = jsch.getSession(user, host, 22);

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect(10000); // 10s timeout
			log.info("[{}] SSH session connected", host);

			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setErrStream(System.err);

			InputStream input = channel.getInputStream();

			channel.connect(5000);
			log.info("[{}] Command sent: {}", host, command);

			// Read STDOUT
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = reader.readLine()) != null) {
				log.info("[STDOUT {}] {}", host, line);
			}

			while (!channel.isClosed()) {
				Thread.sleep(500);
			}

			int exitStatus = channel.getExitStatus();
			if (exitStatus != 0) {
				throw new RuntimeException("SSH command failed on " + host + " (exit=" + exitStatus + ")");
			}

			log.info("[{}] Simulation processes killed successfully", host);
		} finally {
			if (channel != null)
				channel.disconnect();
			if (session != null)
				session.disconnect();
		}
	}
}
