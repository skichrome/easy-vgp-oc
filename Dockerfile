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
	echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDCk0GjpDpMHKcaK9euZor+lTTgR2Z/iVEX8u+ZxpQc6/DW4rc+OJbI3/Iik2BctLsg/3wVeJXwmxI8ugozb+x7y8XeB0xusLTi50oBAj331YktE9T6R5GXT/mLEj+isg163cD+k/yE7bLkpfcTnQxCqbMqSMHg5gFmz6om9Y9jD06JyLz4xBOnNQeAOTcacezj3deWco0Wku5sYhFnwRgPQPhEL04GSsaO0U0siUKTEg3WX2Q+hrvazud6nQCf9myQAt2ikjk8OagDK4CNcIqHIiNvmTutSH3IgWD3UG2cSiCCbeDnhuHdKH76GuAqVdXizfNEpfc3Q66mS9Rve1R3" >> ~/.ssh/authorized_keys && \
	chmod 0600 ~/.ssh/authorized_keys && \
	mkdir /run/sshd && \
	sed -i 's/#Port 22/Port 22/g' /etc/ssh/sshd_config && \
	mkdir /root/.android && \
	touch /root/.android/repositories.cfg

VOLUME $ANDROID_HOME

# Expose ssh port
EXPOSE 22

CMD ["/usr/sbin/sshd", "-D"]
