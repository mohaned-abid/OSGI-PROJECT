**PROTOCOL:XMPP**
**BY: MOHANED ABID ET LAMJED GAIDI INDP3-SNI**



**Configuration**
-installer openfire:serveur XMPP

-telechargement smack v3.0 :client XMPP

-importation des  .jar: smack.jar ,smacks.jar

-configuration de la connexion entre smack et openfire avant l'implementation des bundles





**Création of bundles**
créer le bundle client-XMPP client-XMPP : 

$mvn clean install

créer le bundle EventAdapter EventAdapter : 

$mvn clean install

créer le bundle EventAdmin EventAdmin :  

$mvn clean install





**Exécution of bundles**

1)Lancer OSGi: java -jar org.eclipse.equinox.launcher.jar -console

2)Exécuter les bundles osgi :

$install file:c: path\nom_bundle
