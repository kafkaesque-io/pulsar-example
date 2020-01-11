import pulsar,time
                
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

token='eyJhbGciOiJSUzI PLEASE SUPPLY A VALID TOKEN'

client = pulsar.Client(service_url,
                        authentication=pulsar.AuthenticationToken(token),
                        tls_trust_certs_file_path=trust_certs)

reader = client.create_reader('ming-luo/local-useast2-aws/addressbook_pb2-output', pulsar.MessageId.earliest)

waitingForMsg = True
while waitingForMsg:
    try: 
        msg = reader.read_next(5000)
        print("Received message '{}' id='{}'".format(msg.data(), msg.message_id()))
        # waitingForMsg = False
    except: 
        print("Still waiting for a message...");
    
    time.sleep(1)

client.close()
