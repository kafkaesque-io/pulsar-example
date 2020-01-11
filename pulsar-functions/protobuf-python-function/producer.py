import pulsar
import addressbook_pb2

service_url = 'pulsar+ssl://useast2.aws.kafkaesque.io:6651'

# Use default CA certs for your environment
# RHEL/CentOS:
trust_certs='/etc/ssl/certs/ca-bundle.crt'
# Debian/Ubuntu:
# trust_certs='/etc/ssl/certs/ca-certificates.crt'
# OSX:
# Export the default certificates to a file, then use that file:
#    security find-certificate -a -p /System/Library/Keychains/SystemCACertificates.keychain > ./ca-certificates.crt
# trust_certs='./ca-certificates.crt'

token='eyJhbGciOiJSUzI1NiJ9.eyJzdWI PLEASE SUPPLY A VALID TOKEN'

client = pulsar.Client(service_url,
                        authentication=pulsar.AuthenticationToken(token),
                        tls_trust_certs_file_path=trust_certs)


producer = client.create_producer('ming-luo/local-useast2-aws/addressbook_pb2-input')

address_book = addressbook_pb2.AddressBook()
person = address_book.people.add()
person.id = 123
person.name = "ming"
person.email = "email@email.ca"
number = ""
str = address_book.SerializePartialToString()

producer.send(str.encode('utf-8'))

client.close()
