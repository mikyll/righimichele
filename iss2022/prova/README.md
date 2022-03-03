<h1 align="center">Prova</h1>

### Setup Progetto

1. Inizializzo il progetto Gradle col comando ```gradle init```
	selezioni: 2, 3, 1, 2, 1, \/, \/
2. Eseguo il codice generato con i comandi:
	```bash
	gradlew build -x test  // esclude i test
	gradlew run
	```
3. Se osservo il contenuto del file [app/**build.gradle.kts**](https://github.com/mikyll/righimichele/blob/master/iss2022/prova/app/build.gradle.kts), noto che sono presenti delle direttive organizzate in blocchi. Nel blocco relativo alle *dependencies*, le keyword sono "testImplementation" e "implementation". Questo perché sto usando Gradle 7.\*. Infatti, nelle versioni precedenti (6.\*), queste erano diverse (rispettivamente "testCompile" e "compile"), e sono motivo di errori quando si tenta di eseguire la build di un progetto, usando il comando ```gradlew build```
4. Eseguo i test:
	```bash
	gradlew build   // esegue anche i test
	gradlew test    // esegue solo i test
	```
5. Noto che esiste un file app/build/reports/tests/test/**index.html**. Se lo apro con un browser vedo che è un file formattato per mostrare i risultati dell'ultimo test eseguito.

<p align="center">
	<img align="center" width="60%" src="https://github.com/mikyll/righimichele/blob/master/iss2022/prova/gfx/Test%20results.png"/>
</p>

NB: Navigando l'albero delle sottodirectory del progetto è possibile visualizzare anche i test singoli delle varie classi:

<p align="center">
	<img align="center" width="60%" src="https://github.com/mikyll/righimichele/blob/master/iss2022/prova/gfx/Test%20results%20details.png"/>
</p>
