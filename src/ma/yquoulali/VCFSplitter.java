package ma.yquoulali;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author yquoulali < yquoulali@gmail.com >
 * 
 *         Java class to extract individual VCARD from a file with multiple
 *         cards. <br/>
 *         <h1>Use:
 *         <h1/>
 *         <code>java -jar VCFSpliter /path/to/file.vcf [path/to/output/folder]</code>
 *         <br/>
 *         <h2>If the output folder is not specified, the default one will be
 *         the input file folder.</h2>
 *
 */
public class VCFSplitter {
	private static final String BEGIN = "BEGIN";
	private static String vcfFilePath = null;
	private static String outputPath = null;
	private static String vcfFileName = null;
	private static int nbrImportedVcf = 0;

	public static void main(String[] args) {
		// check the mandatory parameter
		if (args.length < 1 || args.length > 2) {
			System.out.println("Syntax: VCFSpliter /path/to/file.vcf [path/to/output/folder]");
			System.exit(-1);
		}

		initParams(args);

		// check the vcfFile
		checkVcfFile(vcfFilePath);

		try {
			processFile(vcfFilePath);
		} catch (IOException e) {
			System.out.println("An error was occured: " + e.getMessage());
			System.exit(-1);
		}

		System.out.println(nbrImportedVcf + " vCards exported to " + outputPath);
		System.exit(0);
	}

	private static void initParams(String[] args) {
		// 1st arg: vcf file
		vcfFilePath = args[0];

		File vcfFile = new File(vcfFilePath);
		vcfFileName = vcfFile.getName().substring(0, vcfFile.getName().indexOf(".vcf"));
		// 2nd arg - optional - output folder
		if (args.length == 2) {
			outputPath = args[1];
			// check the output folder
			checkOutputFolder(outputPath);
		} else {
			outputPath = vcfFile.getParent();
		}
	}

	private static void processFile(String vcfFile) throws FileNotFoundException, IOException {
		BufferedWriter writer = null;
		try (BufferedReader br = new BufferedReader(new FileReader(vcfFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				// check if it is a new VCARD
				if (line.startsWith(BEGIN)) {
					nbrImportedVcf++;

					// init the writer
					if (writer != null) {
						writer.flush();
						writer.close();
					}
					File newVcfFile = getNewVcfFile(nbrImportedVcf);
					writer = new BufferedWriter(new FileWriter(newVcfFile));

				}

				// append the line to the writer
				append(writer, line);

			}
		}

		// END
		if (writer != null) {
			writer.flush();
			writer.close();
		}

	}

	private static File getNewVcfFile(int nbrImportedVcf) {

		File newVcfFile = new File(outputPath, vcfFileName + "-" + nbrImportedVcf + ".vcf");
		return newVcfFile;
	}

	private static void checkVcfFile(String vcfFile2) {
		File file = new File(vcfFile2);
		if (!file.exists()) {
			System.out.println(vcfFile2 + " does not exist");
			System.exit(-1);
		}
		if (file.isDirectory()) {
			System.out.println(vcfFile2 + " is a directory");
			System.exit(-1);
		}
	}

	private static void checkOutputFolder(String outputPath2) {
		File file = new File(outputPath2);
		if (!file.exists()) {
			file.mkdirs();
		}
		if (!file.isDirectory()) {
			System.out.println(file + " is not a folder");
			System.exit(-1);
		}
	}

	private static void append(BufferedWriter writer, String line) throws IOException {
		writer.append(line);
		writer.newLine();
	}

}
