**** DEPLOY JARS ****
- execute build.bat, which performs >mvn package< to deploy all necessary .jar files in the target folder
HINT: This only works if you have the path to the maven binaries in your PATH (in Windows)

**** EXECUTION WORKFLOW ****
The batch-files to execute our implementation are in the project's target folder. The files prefix 
(in the following list replaced with *) suggest the used technology, followed by the entity's name.
The GUI for the market and the investors can be executed from the respective -jar-files.
- *_Server.bat: Server that has to be started first for the actors to work properly.
- *_Broker.bat: Broker that handles the transaction logic for the stock market.
- *_Company_<CompanyName>.bat: Starts a single company and issues a stock request to the broker.
- *_MarketAgent.bat: Starts the market agent, which influences the stock prices actively.
- MarketInvestorGUI.jar: Starts the GUI for to investigate current stock prices, the market volume of each stock, as well as
trade orders of all companies/investors and a transaction history.
- InvestorGUI.jar: Starts the Investor GUI, which enables investors to manage their budget, orders and stocks.
