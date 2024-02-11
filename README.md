LangChain4j Demo Project

Description:
- A simple implementation of the LangChain4j AIService, which is prompted to give information about the pdf file located in the resources/documents folder.
- To communicate with the AIService, you can use the provided chat app OR directly through the endpoint.

Used tech:
- LLM: Ollama with Llama2 OR Mistral model
- Database: Qdrant
- Chat: Vaadin

Docker:
- Both the model and the database are running in a seperate docker container as services with default settings.

Configuration:
- See the application.properties file.

Note:
- I case of some kind of "test_collection" error, just run the app again.
- The problem is with the deleteCollection() method of the qdrant DB, it's sometimes not working properly, but its necessary to reset the collection before the embedding of the pdf.
