LangChain4j Demo Project

Description:
- A simple implementation of the LangChain4j AIService, which is prompted to give information about the pdf file located in the resources/documents folder.
- To communicate with the AIService, you can use the provided chat app OR directly through the endpoint.
- Chat app: localhost:8080
- Endpoint: localhost:8080/api/message 

Used tech:
- LLM: Ollama
- Database: Qdrant
- Chat: Vaadin

Configuration:
- See the application.properties file.
- model_name can be change for whatever model you would like to use that is supported by ollama.

Docker:
- Both the model and the database are running in a seperate docker container as services with default settings (see compose.yaml).
- In case of the used model (default: mistral) is not pulled for the ollama service, use the following command: ollama run [model_name]

Notes:
- In case of some kind of "test_collection" error, just run the app again.
- The problem is with the deleteCollection() method of the qdrant database, it's sometimes not working properly, but its necessary to reset the collection before the embedding of the pdf file.

Possible future updates:
- rework the database logic or replace the database bacause of the problem listed in the Notes section.
- adding file-upload feature to the ChatUI, so the app can work with any document provided by the user.
- adding customizable prompt/system message feature to the ChatUI, so the user can costumize the AIService based on the provided content.
