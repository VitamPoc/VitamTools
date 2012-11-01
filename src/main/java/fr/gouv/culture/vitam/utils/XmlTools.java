/**
 * This file is part of Vitam Project.
 * 
 * Copyright 2010, Frederic Bregier, and individual contributors by the @author
 * tags. See the COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 * 
 * All Vitam Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Vitam is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Vitam. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fr.gouv.culture.vitam.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.thaiopensource.validate.ValidationDriver;

import fr.gouv.culture.vitam.saxhandlers.SaxErrorHandler;
import fr.gouv.culture.vitam.saxhandlers.VitamUriResolver;
import fr.gouv.culture.vitam.saxhandlers.XsdValidContentHandler;

/**
 * XML manipulation class using SAX
 * 
 * @author "Frederic Bregier"
 * 
 */
public class XmlTools {
	/**
	 * XML Validation
	 * 
	 * @param current_file
	 * @param config
	 * @return True if ok
	 */
	static public boolean xml_validation(File current_file, ConfigLoader config) {
		boolean hadError = false;
		XMLReader parser = null;
		try {
			parser = XMLReaderFactory.createXMLReader();
			SaxErrorHandler handler = new SaxErrorHandler();
			parser.setErrorHandler(handler);
			FileInputStream in = null;
			try {
				in = new FileInputStream(current_file);
				parser.parse(new InputSource(in));
			} catch (SAXParseException e) {
				// Deja pris en charge par le ErrorHandler
			} catch (Exception e) {
				System.err.println(StaticValues.LBL.error_walk.get() + " - "
						+ e.toString());
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
			hadError = handler.hadError();
		} catch (Exception e) {
			hadError = true;
			System.err.println(StaticValues.LBL.error_error.get() + " " + e
					+ " - " +
					StaticValues.LBL.error_parser.get() + " ("
					+ config.DEFAULT_PARSER_NAME + ")");
		}
		return !(hadError);
	}

	/**
	 * XSD Validation
	 * 
	 * @param current_file
	 * @param config
	 * @return True if ok
	 */
	static public boolean xsd_validation(File current_file, ConfigLoader config) {
		boolean hadError = false;
		XMLReader parser = null;
		try {
			parser = XMLReaderFactory.createXMLReader();
			try {
				String schema = null;
				if (config.CURRENT_XSD != null) {
					schema = new File(config.CURRENT_XSD).toURI().toString();
				} else {
					schema = parser.getClass().getResource(StaticValues.DEFAULT_XSD).toString();
				}
				String schemaLocation = config.DEFAULT_LOCATION + " " + schema;
				parser.setProperty(StaticValues.EXTERNAL_SCHEMALOCATION, schemaLocation);
			} catch (Exception e) {
				System.err.println(StaticValues.LBL.error_location.get() + " ("
						+ StaticValues.EXTERNAL_SCHEMALOCATION
						+ ")");
			}
			parser.setFeature(StaticValues.HTTP_XML_ORG_SAX_FEATURES_VALIDATION, true);
			try {
				parser.setFeature(StaticValues.SCHEMA_VALIDATION_FEATURE_ID, true);
			} catch (Exception e) {
				System.err.println(StaticValues.LBL.error_function.get() + " ("
						+ StaticValues.SCHEMA_VALIDATION_FEATURE_ID + ")");
			}
			SaxErrorHandler handler = new SaxErrorHandler();
			XsdValidContentHandler xsdValidContentHandler = new XsdValidContentHandler(config);
			parser.setErrorHandler(handler);
			parser.setContentHandler(xsdValidContentHandler);
			FileInputStream in = null;
			try {
				in = new FileInputStream(current_file);
				parser.parse(new InputSource(in));
			} catch (SAXParseException e) {
				// Deja pris en charge par le ErrorHandler
			} catch (Exception e) {
				System.err.println(StaticValues.LBL.error_walk.get() + " - "
						+ e.toString());
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
			hadError = handler.hadError() || xsdValidContentHandler.hadError();
		} catch (Exception e) {
			hadError = true;
			System.err.println(StaticValues.LBL.error_error.get() + " " + e
					+ " - " +
					StaticValues.LBL.error_parser.get() + " ("
					+ config.DEFAULT_PARSER_NAME + ")");
		}
		return !(hadError);
	}

	/**
	 * Schematron validation
	 * 
	 * @param current_file
	 * @param schematron
	 *            as a File
	 * @param config
	 * @return True if OK
	 */
	static public boolean schematron_validation(File current_file, File schematron,
			ConfigLoader config) {
		FileInputStream in;
		try {
			in = new FileInputStream(schematron);
		} catch (FileNotFoundException e) {
			System.err.println(e.toString());
			return false;
		}
		return schematron_validation(current_file, in, config);
	}

	/**
	 * Schematron validation
	 * 
	 * @param current_file
	 * @param schematron
	 *            InputStream
	 * @param config
	 * @return True if OK
	 */
	static public boolean schematron_validation(File current_file, InputStream schematron,
			ConfigLoader config) {
		boolean hadError = false;
		InputStream in1_1 = null;
		InputStream in1_2 = null;
		InputStream in1_3 = null;
		FileInputStream in = null;
		InputStream in2_2 = null;
		String ancienPath = SystemPropertyUtil.set("user.dir", current_file.getParent());
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			XMLReader reader = spf.newSAXParser().getXMLReader();
			spf = null;
			SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory
					.newInstance();
			factory.setURIResolver(new VitamUriResolver("/resources/schematron/"));
			Transformer transformer = factory.newTransformer();
			ByteArrayOutputStream writer = new ByteArrayOutputStream();

			// etape 1
			in1_1 = current_file.getClass()
					.getResourceAsStream(StaticValues.RESOURCES_SCHEMATRON_ISO_DSDL_INCLUDE_XSL);
			XMLFilter filtre1_1 = factory.newXMLFilter(new StreamSource(in1_1));
			in1_2 = current_file.getClass()
					.getResourceAsStream(StaticValues.RESOURCES_SCHEMATRON_ISO_ABSTRACT_EXPAND_XSL);
			XMLFilter filtre1_2 = factory.newXMLFilter(new StreamSource(in1_2));
			if (config.useXslt1ForSchematron) {
				in1_3 = current_file.getClass()
						.getResourceAsStream(
								StaticValues.RESOURCES_SCHEMATRON_ISO_SVRL_FOR_XSLT1_XSL);
			} else {
				in1_3 = current_file.getClass()
						.getResourceAsStream(
								StaticValues.RESOURCES_SCHEMATRON_ISO_SVRL_FOR_XSLT2_XSL);
			}
			XMLFilter filtre1_3 = factory.newXMLFilter(new StreamSource(in1_3));

			filtre1_1.setParent(reader);
			filtre1_2.setParent(filtre1_1);
			filtre1_3.setParent(filtre1_2);
			SAXSource source = new SAXSource(filtre1_3, new InputSource(schematron));
			StreamResult resultat = new StreamResult(writer);

			transformer.transform(source, resultat);

			// recuperation du resulat
			byte[] content = writer.toByteArray();
			writer.reset();

			filtre1_1 = null;
			filtre1_2 = null;
			filtre1_3 = null;
			source = null;
			resultat = null;
			// close inputStreams
			try {
				in1_1.close();
				in1_1 = null;
			} catch (IOException e) {
			}
			try {
				in1_2.close();
				in1_2 = null;
			} catch (IOException e) {
			}
			try {
				in1_3.close();
				in1_3 = null;
			} catch (IOException e) {
			}
			try {
				schematron.close();
			} catch (IOException e) {
			}

			// etape 2
			XMLFilter filtre2_1 = factory.newXMLFilter(new StreamSource(new ByteArrayInputStream(
					content)));
			in2_2 = current_file.getClass()
					.getResourceAsStream(StaticValues.RESOURCES_SCHEMATRON_SHOW_ERRORS_XSL);
			XMLFilter filtre2_2 = factory.newXMLFilter(new StreamSource(in2_2));

			filtre2_1.setParent(reader);
			filtre2_2.setParent(filtre2_1);

			in = new FileInputStream(current_file);
			SAXSource source2 = new SAXSource(filtre2_2, new InputSource(in));
			StreamResult resultat2 = new StreamResult(writer);

			transformer.setOutputProperty("omit-xml-declaration", "yes");
			transformer.transform(source2, resultat2);

			content = null;
			reader = null;
			factory = null;
			transformer = null;
			filtre2_1 = null;
			filtre2_2 = null;
			source2 = null;
			resultat2 = null;
			// close inputStreams
			try {
				in2_2.close();
				in2_2 = null;
			} catch (IOException e) {
			}
			try {
				in.close();
				in = null;
			} catch (IOException e) {
			}

			// affichage du resultat
			String res = writer.toString(StaticValues.CURRENT_OUTPUT_ENCODING);
			if (!res.trim().equals("")) {
				String[] values = res.split(" ");
				if (values.length > 2) {
					if (!values[2].trim().equals("0")) {
						// error
						hadError = true;
						System.err.println(res);
					}
				} else {
					hadError = true;
					System.err.println(res);
				}
			}
			writer.close();
			writer.reset();
			writer = null;
		} catch (Exception e) {
			hadError = true;
			System.err.println(e.toString());
		} finally {
			if (in1_1 != null) {
				try {
					in1_1.close();
				} catch (IOException e) {
				}
			}
			if (in1_2 != null) {
				try {
					in1_2.close();
				} catch (IOException e) {
				}
			}
			if (in1_3 != null) {
				try {
					in1_3.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (in2_2 != null) {
				try {
					in2_2.close();
				} catch (IOException e) {
				}
			}
			try {
				schematron.close();
			} catch (IOException e) {
			}
			SystemPropertyUtil.set("user.dir", ancienPath);
		}
		return !(hadError);
	}

	/**
	 * Profil validation
	 * 
	 * @param current_file
	 * @param rng
	 * @return True if OK
	 */
	static public boolean profil_validation(File current_file, File rng) {
		boolean hadError = false;
		try {
			ValidationDriver driver = new ValidationDriver();
			InputSource in = ValidationDriver.fileInputSource(rng);
			if (driver.loadSchema(in)) {
				if (!driver.validate(ValidationDriver.uriOrFileInputSource(current_file.toURI()
						.toString())))
					hadError = true;
			} else
				hadError = true;
		} catch (SAXException e) {
			hadError = true;
			System.err.println(StaticValues.LBL.error_walk.get() + " "
					+ e.toString());
		} catch (IOException e) {
			System.err.println(StaticValues.LBL.error_walk.get() + " "
					+ e.toString());
			hadError = true;
		}
		return !(hadError);
	}

	/**
	 * 
	 * @param name1
	 * @param name2
	 * @return the combination of the 2 file name into one as name1_name2 without extension
	 */
	public static final String getNameFrom2Files(String name1, String name2) {
		int position = name1.lastIndexOf('.');
		if (position < 0) {
			position = name1.length();
		}
		int position2 = name2.lastIndexOf('.');
		if (position2 < 0) {
			position2 = name2.length();
		}
		int slashpos = name2.lastIndexOf('\\') + 1;
		if (slashpos <= 0) {
			slashpos = name2.lastIndexOf('/') + 1;
			if (slashpos < 0) {
				slashpos = 0;
			}
		}
		return name1.substring(0, position) + "_" + name2.substring(slashpos, position2);
	}

	/**
	 * 
	 * @param current_file
	 * @param xsl
	 * @param config
	 * @param outputname
	 * @return the File transformed
	 */
	static public File transform(File current_file, File xsl, ConfigLoader config, String outputname) {
		FileInputStream in;
		try {
			in = new FileInputStream(xsl);
		} catch (FileNotFoundException e) {
			System.err.println(e.toString());
			return null;
		}
		return transform(current_file, in, config, outputname);
	}

	/**
	 * XSL transformation
	 * 
	 * @param current_file
	 * @param xsl
	 * @param config
	 * @param outputname
	 * @return the file output if ok
	 */
	static public File transform(File current_file, InputStream xsl, ConfigLoader config,
			String outputname) {
		FileInputStream in = null;
		FileOutputStream outstream = null;
		String ancienPath = SystemPropertyUtil.set("user.dir", current_file.getParent());
		try {
			in = new FileInputStream(current_file);
			SAXSource source = new SAXSource(new InputSource(in));
			File out = new File(current_file.getParentFile(), outputname);
			outstream = new FileOutputStream(out);
			StreamResult result = new StreamResult(outstream);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(xsl));
			transformer.setURIResolver(new VitamUriResolver(new String[] { config.BASE_RESOURCES,
					StaticValues.resourceToParent(StaticValues.XSD2SCHEMA),
					current_file.getParentFile().toURI().getPath() }));
			transformer.transform(source, result);
			return out;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (outstream != null) {
				try {
					outstream.close();
				} catch (IOException e) {
				}
			}
			if (xsl != null) {
				try {
					xsl.close();
				} catch (IOException e) {
				}
			}
			SystemPropertyUtil.set("user.dir", ancienPath);
		}
	}

	/**
	 * XSL transformation in house
	 * 
	 * @param inString
	 * @param xsl
	 * @param config
	 * @return the String as result
	 */
	static public String transform(String inString, File xsl, ConfigLoader config) {
		FileInputStream in;
		try {
			in = new FileInputStream(xsl);
		} catch (FileNotFoundException e) {
			System.err.println(e.toString());
			return null;
		}
		return transform(inString, in, config);
	}

	/**
	 * XSL transformation in house
	 * 
	 * @param inString
	 * @param xsl
	 * @param config
	 * @return the String as result
	 */
	static public String transform(String inString, InputStream xsl, ConfigLoader config) {
		ByteArrayInputStream in = null;
		ByteArrayOutputStream outstream = null;
		try {
			in = new ByteArrayInputStream(inString.getBytes());
			SAXSource source = new SAXSource(new InputSource(in));
			outstream = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(outstream);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(xsl));
			transformer.setURIResolver(new VitamUriResolver(new String[] { config.BASE_RESOURCES,
					StaticValues.resourceToParent(StaticValues.XSD2SCHEMA) }));
			transformer.transform(source, result);
			return outstream.toString(StaticValues.CURRENT_OUTPUT_ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (outstream != null) {
				try {
					outstream.close();
				} catch (IOException e) {
				}
			}
			if (xsl != null) {
				try {
					xsl.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * get XSModel from xsd file
	 * 
	 * @param file
	 * @return the XSModel
	 */
	static public XSModel getXSModelFromXsdFile(File file) {
		XMLSchemaLoader xsloader = new XMLSchemaLoader();
		return xsloader.loadURI(file.toURI().toString());
	}

	/**
	 * Given an SchemaGrammar, returns a list of the root element names that a Document can have
	 * associated as children. Candidates: those top elements that doesn't need to belong to another
	 * elements...
	 * 
	 * @param _sg
	 *            the XSModel (from getXSModelFromXsdFile)
	 * @return the vector of potential root from XSModel
	 */
	static public List<String> getRootElementNames(XSModel _sg) {
		List<String> ret = new ArrayList<String>();
		HashSet<String> referencedElements = new HashSet<String>();
		HashSet<String> visitedTypes = new HashSet<String>();
		// Get all schema root elements (defined)
		XSNamedMap elementDeclarations = _sg.getComponents(XSConstants.ELEMENT_DECLARATION);
		int n = elementDeclarations.getLength();
		// Visit every top element in grammar. For each of them, store
		// all elements referenced in those types.
		for (int i = 0; i < n; i++) {
			XSObject xso = elementDeclarations.item(i);
			XSElementDeclaration xsed = (XSElementDeclaration) xso;
			XSTypeDefinition xtd = xsed.getTypeDefinition();
			if (xtd != null) {
				storeReferencedElements(xtd, visitedTypes, referencedElements);
			}
		}
		// Make a 2nd visit to fill vector of root element names to return.
		for (int i = 0; i < n; i++) {
			XSObject xso = elementDeclarations.item(i);
			boolean isNotRoot = referencedElements.contains(xso.getName());
			if (!isNotRoot) {
				ret.add(xso.getName());
			}
		}
		return ret;
	}

	/**
	 * For the given type, store referenced elements in referencedElements hashtable
	 */
	static private void storeReferencedElements(XSTypeDefinition xtd, HashSet<String> visitedTypes,
			HashSet<String> referencedElements) {
		// REVISIT: a) Do not use hashtable attribute, but a dynamic one OR b) synchronize methods
		String typeName = xtd.getName();
		// To avoid a named type to be revisited several times, we mark current type as a visited
		// one...
		// For in-line (un-named) types there's no solution at all...
		if (typeName != null && visitedTypes.contains(typeName)) {
			return;
		}
		// REVISIT:
		// A) Foo value 'v'. We should use a vector of unique values (now hash keys...)
		// B) Keep NameSpaces in mind as well!!
		if (typeName != null) {
			visitedTypes.add(typeName);
		}
		// REVISIT: ? Used better than: if(xtd.getTypeCategory()==XSTypeDefinition.COMPLEX_TYPE)
		if (xtd instanceof XSComplexTypeDecl) {
			XSComplexTypeDecl xtdc = (XSComplexTypeDecl) xtd;
			XSParticle xsp = xtdc.getParticle();
			if (xsp != null) {
				refElemsFromParticle(xsp, visitedTypes, referencedElements);
			}
		} // else ... REVISIT: Simple types may hide some valueable info?
	}

	static private void refElemsFromParticle(XSParticle xsp, HashSet<String> visitedTypes,
			HashSet<String> referencedElements) {
		XSTerm xsterm = xsp.getTerm();
		// If it is a complexType, call this very method for every particle
		if (xsterm instanceof XSModelGroupImpl) {
			// Obtain particles for this group implementation
			XSModelGroupImpl xmgi = (XSModelGroupImpl) xsterm;
			XSObjectList ol = xmgi.getParticles();
			if (ol != null) {
				int n = ol.getLength();
				for (int j = 0; j < n; j++) {
					Object o = ol.item(j);
					refElemsFromParticle((XSParticle) o, visitedTypes, referencedElements);
				}
			}
		}
		// If it is an element: A) store its name in referencedElements hash and
		// b) visit its type as well, searching other referenced elements.
		else if (xsterm instanceof XSElementDecl) {
			XSElementDecl xeldec = (XSElementDecl) xsterm;
			String elemName = xeldec.getName();
			// REVISIT: Foo value 'r'. We should use a vector of unique values (now hash keys...)
			referencedElements.add(elemName);
			XSTypeDefinition xtd = xeldec.getTypeDefinition();
			if (xtd != null) {
				storeReferencedElements(xtd, visitedTypes, referencedElements);
			}
		}
	}

	/**
	 * Better method to get root elements from a XSD schema
	 * 
	 * @param file
	 * @param config
	 * @return list of root
	 * @throws JDOMException
	 * @throws IOException
	 * @throws TransformerException
	 */
	public static List<String> getRootElementNamesUsingSaxon(File file, ConfigLoader config)
			throws JDOMException, IOException,
			TransformerException {
		Map<String, String> param = new HashMap<String, String>();
		String path = file.getParentFile().toURI().getPath();
		param.put("path", path); //$NON-NLS-1$
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document document = builder.build(file);
		JDOMSource source = new JDOMSource(document);
		JDOMResult res = new JDOMResult();
		Transformer transformer = getTransformer(path, param, config);
		transformer.transform(source, res);
		Document result = res.getDocument();
		param.clear();
		param = null;
		List<String> list = setCombos(result);
		transformer.reset();
		transformer = null;
		document.removeContent();
		document = null;
		result.removeContent();
		result = null;
		builder = null;
		source = null;
		res = null;
		StaticValues.freeMemory();
		return list;
	}

	private static Transformer getTransformer(String path, Map<String, String> param,
			ConfigLoader config)
			throws TransformerConfigurationException {
		Transformer transformer = null;
		Templates templates = null;
		System.setProperty(StaticValues.TRANSFORMER_FACTORY,
				StaticValues.SAXON_TRANSFORMER_NAME);
		TransformerFactory tfactory = TransformerFactory.newInstance();
		templates = tfactory.newTemplates(new StreamSource(
				StaticValues.class.getResourceAsStream(
						StaticValues.XSD2SCHEMA)));
		transformer = templates.newTransformer();
		transformer.setURIResolver(new VitamUriResolver(new String[] { config.BASE_RESOURCES,
				StaticValues.resourceToParent(StaticValues.XSD2SCHEMA), path }));
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		Object[] keys = param.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			transformer.setParameter(keys[i].toString(),
					param.get(keys[i]));
		}
		keys = null;
		templates = null;
		tfactory = null;
		System.setProperty(StaticValues.TRANSFORMER_FACTORY,
				config.DEFAULT_TRANSFORMER_NAME);
		return transformer;
	}

	private static List<String> setCombos(Document doc) {
		@SuppressWarnings("unchecked")
		List<Element> children = doc.getRootElement().getChildren();
		List<String> listRootElements = new ArrayList<String>();
		if (children.size() > 0) {
			for (Element child : children) {
				if (child.getName().equals("element") || child.getName().equals("complexType")) { //$NON-NLS-1$ //$NON-NLS-2$
					Attribute attr = child.getAttribute("name"); //$NON-NLS-1$
					listRootElements.add(attr.getValue());
				}
			}
		}
		children.clear();
		children = null;
		return listRootElements;
	}

}
