package org.magneato.utils;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class ObjectUnmarshaller<T> {
	XMLStreamReader reader;
	Class<T> c;
	Unmarshaller unmarshaller;

	public ObjectUnmarshaller(InputStream stream, Class<T> c)
			throws XMLStreamException, FactoryConfigurationError, JAXBException {
		this.c = c;
		unmarshaller = JAXBContext.newInstance(c).createUnmarshaller();

		reader = XMLInputFactory.newInstance().createXMLStreamReader(
				stream);

		skipElements(XMLStreamReader.START_DOCUMENT);
		reader.next(); // skip root element <pages>
	}

	public T next() throws XMLStreamException, JAXBException {

		skipElements(XMLStreamReader.END_ELEMENT, XMLStreamReader.CHARACTERS,
				XMLStreamReader.END_DOCUMENT);
		if (reader.hasNext()) {
			T value = unmarshaller.unmarshal(reader, c).getValue();
			return value;
		}
		return null;
	}

	public void close() throws XMLStreamException {
		reader.close();
	}

	public void skipElements(int... elements) throws XMLStreamException {
		int eventType = reader.getEventType();

		while (contains(eventType, elements)) {
			if (reader.hasNext()) {
				eventType = reader.next();
			} else {
				return;
			}
		}
	}

	private boolean contains(int eventType, int[] elements) {

		for (int element : elements) {
			if (eventType == element) {
				return true;
			}
		}// for
		return false;
	}
}
