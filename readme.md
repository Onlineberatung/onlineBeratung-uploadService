# Online-Beratung UploadService

The UploadService acts as a middleman between the client and Rocket.Chat to upload files to Rocket.Chat.
It was introduced because the Rocket.Chat API also allows to send a message within the same file upload call and therefore the de- and encryption of this message needs to be done with the same routine the MessageService does.

Furthermore you can limit the number of uploads and the files types that can be uploaded with the help of the UploadService.

## Help and Documentation
In the project [documentation](https://onlineberatung.github.io/documentation/docs/setup/setup-backend) you'll find information for setting up and running the project.
You can find some detailled information of the service architecture and its processes in the repository [documentation](https://github.com/Onlineberatung/onlineBeratung-uploadService/tree/master/documentation).

## License
The project is licensed under the AGPLv3 which you'll find [here](LICENSE.md).

## Code of Conduct
Please have a look at our [Code of Conduct](https://github.com/Onlineberatung/.github/blob/master/CODE_OF_CONDUCT.md) before participating in the community.

## Contributing
Please read our [contribution guidelines](https://github.com/Onlineberatung/.github/blob/master/CONTRIBUTING.md) before contributing to this project.
