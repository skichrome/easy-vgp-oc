# skichrome/android-sdk:1.0.3

# Base image
FROM debian:9

# Working directory
RUN mkdir /application
WORKDIR /application

#Required environment variables
ENV ANDROID_HOME /opt/android-sdk
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

# Variables, default or from arguments
ENV ANDROID_BUILD_VERSION="29"
ENV ANDROID_BUILD_TOOLS_VERSION="29.0.2"
ENV ANDROID_EMULATOR_IMG_TYPE="google_apis"
ENV ANDROID_EMULATOR_ARCH="x86"
ENV EMULATOR_NAME="emulateur"

# Update and install required libraries
RUN dpkg --add-architecture i386 && \
        apt-get update && \
		apt-get upgrade -y --no-install-recommends && \
		apt-get install -y --no-install-recommends libncurses5:i386 \
        libc6:i386 \
        libstdc++6:i386 \
        lib32gcc1 \
        lib32ncurses5 \
        lib32z1 \
        zlib1g:i386 \
		qt5-default && \
		apt-get autoremove -y --no-install-recommends && \
		apt-get autoclean && \
		apt-get install -y --no-install-recommends default-jdk \
			wget \
			nano \
			git \
			unzip \
			openssh-server

# SSH public Key and server Configuration
RUN mkdir /root/.ssh/ && \
	echo "ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQEAs3O8CEKsqIpw3stHGmtd1jzgNIrNF+z7bn/qnmMPKVZZinDGr93hiAArkNbW2k3YLkd76CJuzO0w0YbOKYHK2PISFvK7+cc1SmCexmXXs9wHYdG61ZIWAq2HfIPndx9ZSUiet629wc9Q06hoL/IDUgkQAeCbAQSci6HhbV5SPmsSCdM9ToqmNpRJ3BdDfXn32mvxyejP8cw9MrJDtBY/pvXjiJRWTwHdErIRKe4foYxXrAXTPRkesntxc6ikX9qlKNmzEuJgOWwPHauOjBWvp5VdgsVBHVQsjI0AA7V4aMQTFqsQz6c+8d1VVES+EnpP4PTJmNP/ImaaB/JTZP8eOw==" > ~/.ssh/authorized_keys && \
	echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDCk0GjpDpMHKcaK9euZor+lTTgR2Z/iVEX8u+ZxpQc6/DW4rc+OJbI3/Iik2BctLsg/3wVeJXwmxI8ugozb+x7y8XeB0xusLTi50oBAj331YktE9T6R5GXT/mLEj+isg163cD+k/yE7bLkpfcTnQxCqbMqSMHg5gFmz6om9Y9jD06JyLz4xBOnNQeAOTcacezj3deWco0Wku5sYhFnwRgPQPhEL04GSsaO0U0siUKTEg3WX2Q+hrvazud6nQCf9myQAt2ikjk8OagDK4CNcIqHIiNvmTutSH3IgWD3UG2cSiCCbeDnhuHdKH76GuAqVdXizfNEpfc3Q66mS9Rve1R3" >> ~/.ssh/authorized_keys && \
	chmod 0600 ~/.ssh/authorized_keys && \
	mkdir /run/sshd && \
	sed -i 's/#Port 22/Port 22/g' /etc/ssh/sshd_config && \
	mkdir /root/.android && \
	touch /root/.android/repositories.cfg

# Download Android SDK and create symlinks
RUN mkdir -p ${ANDROID_HOME} && cd ${ANDROID_HOME} && \
	wget -q https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip && \
    unzip *tools*linux*.zip && \
    rm *tools*linux*.zip && \
	ln -s ${ANDROID_HOME}/platform-tools/adb /usr/bin/adb && \
	ln -s ${ANDROID_HOME}/tools/bin/sdkmanager /usr/bin/sdkmanager

VOLUME $ANDROID_HOME

# jenkins:x:114:120:Jenkins
RUN useradd --no-create-home --uid 114 --no-log-init jenkins
# Expose adb ports
EXPOSE 22
EXPOSE 5037
EXPOSE 5554
EXPOSE 5555

CMD ["/usr/sbin/sshd", "-D"]
