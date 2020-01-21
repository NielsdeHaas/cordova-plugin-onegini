/*
 * Copyright (c) 2017-2019 Onegini B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

const fs = require('fs');
const path = require('path');
const spawn = require('child_process').spawn;

const supportedPlatforms = ['android', 'ios'];
const envVariables = {
  enableAutoconfigure: 'ONEGINI_AUTOCONFIGURE',
  configuratorName: 'ONEGINI_SDK_CONFIGURATOR_PATH',
  configFiles: {
    android: 'ONEGINI_CONFIG_ANDROID_PATH',
    ios: 'ONEGINI_CONFIG_IOS_PATH'
  }
};
const extractedConfigPlugin = 'cordova-plugin-onegini-extracted-config';

module.exports = function (context) {
  if (process.env.ONEGINI_AUTOCONFIGURE === "false") {
    console.log('ONEGINI_AUTOCONFIGURE is set to false. Skipping Onegini SDK Configuration');
    return;
  }

  const projectRoot = context.opts.projectRoot;
  const hasExtractedConfig = hasExtractedConfigFiles(context);
  const args = ['--cordova', '--app-dir', projectRoot];
  let platform = context.opts.plugin.platform;

  // Don't trigger the iOS SDK configurator during the 'after_plugin_install' lifecycle phase as it will be triggered again during the 'after_platform_add'
  // lifecycle phase.
  // There is a caveat if 'cordova platform add ios android' is triggered. In this the configurator for iOS can't be suppressed for the iOS platform only.
  // Hence, this command will execute the SDK configurator twice for the iOS platform. It does not break anything but is merely redundant.
  if (afterPluginInstallHookTriggeredDuringPlatformInstallOnlyForIos(context)) {
    return;
  }

  // Only trigger the SDK configurator during the 'after_platform_add' lifecycle phase for the iOS platform in case the
  // cordova command 'cordova platform add ios' is triggered.
  if (afterPlatformAddHookTriggeredDuringPlatformAddAndIosPlatformInstalled(context, projectRoot)) {
    // the platform is not provided in the context for the 'cordova platform add <platform>' command so it is set manually.
    platform = 'ios';
  }
  else if (platform === undefined || platform === 'undefined') {
    return;
  }

  console.log('==============================================' + new Array(platform.length).join('='));
  console.log(`Configuring the Onegini SDK for the ${platform} platform`);
  console.log('----------------------------------------------' + new Array(platform.length).join('-') + '\n\n');

  if (supportedPlatforms.indexOf(platform) < -1) {
    console.log(`${platform} is not supported`);
    return;
  }

  let platformArgs = args.slice();
  platformArgs.unshift(platform);
  const configFile = getConfigFileForPlatform(projectRoot, platform, hasExtractedConfig);
  platformArgs.push('--config', configFile);

  return execConfigurator(projectRoot, platform, hasExtractedConfig, platformArgs);
};

function afterPluginInstallHookTriggeredDuringPlatformInstallOnlyForIos(context) {
  let afterPluginInstallHookTriggered = context.hook === 'after_plugin_install';
  const platformAddCommandTriggeredOnlyForIos =
    context.cmdLine.includes('platform add')
    && context.cmdLine.includes('ios')
    && !context.cmdLine.includes('android');

  return (afterPluginInstallHookTriggered && platformAddCommandTriggeredOnlyForIos)
}

function afterPlatformAddHookTriggeredDuringPlatformAddAndIosPlatformInstalled(context, projectRoot) {
  const afterPlatformAddHookTriggered = context.hook === 'after_platform_add';
  const iosPlatformInstalled = fs.existsSync(path.join(projectRoot, 'platforms', 'ios'));
  const platformAddCommandTriggeredForIos = context.cmdLine.includes('platform add') && context.cmdLine.includes('ios');

  return (afterPlatformAddHookTriggered && iosPlatformInstalled && platformAddCommandTriggeredForIos);
}

function executeCommand(command, args) {
  return new Promise(function (resolve, reject) {
    console.log('\nRunning command:');
    console.log(`${command} ${args.join(' ')}\n`);

    const configurator = spawn(command, args);

    configurator.stdout.on('data', (data) => {
      process.stdout.write(data);
    });

    configurator.stderr.on('data', (data) => {
      process.stdout.write(data);
    });

    configurator.on('close', (code) => {
      if (code === 0) {
        resolve("finished");
      }
      else {
        reject("failed");
      }
    });
  });
}

function execConfigurator(projectRoot, platform, hasExtractedConfig, args) {
  const configuratorName = getConfiguratorName(projectRoot, platform, hasExtractedConfig);
  return executeCommand(configuratorName, args)
    .then(copyArtifactoryCredentials(projectRoot, platform, hasExtractedConfig))
    .then(value => {
      console.log('==============================================' + new Array(platform.length).join('='));
      return value;
    }, reason => {
      console.log('==============================================' + new Array(platform.length).join('=') + '\n');
      throw new Error('Could not configure the Onegini SDK with your configuration');
    });
}

function getConfigFileForPlatform(projectRoot, platform, hasExtractedConfig) {
  const environmentVar = envVariables.configFiles[platform];
  const environmentLocation = process.env[environmentVar];
  const extractedConfigPluginLocation = `${projectRoot}/plugins/${extractedConfigPlugin}/onegini-config-${platform}.zip`;
  const defaultLocation = `${projectRoot}/onegini-config-${platform}.zip`;

  if (environmentLocation) {
    console.log(`Using Token Server config zip: '${environmentLocation}' set in '${environmentVar}'`);
    return environmentLocation;
  }

  if (hasExtractedConfig) {
    console.log(`Using Token Server config zip from '${extractedConfigPluginLocation}'`);
    return extractedConfigPluginLocation;
  }

  console.log(`Using default Token Server config zip: '${defaultLocation}'`);
  return defaultLocation;
}

function getConfiguratorName(projectRoot, platform, hasExtractedConfig) {
  const environmentVar = envVariables.configuratorName;
  const environmentName = process.env[environmentVar];
  const extractedName = `${projectRoot}/plugins/${extractedConfigPlugin}/onegini-sdk-configurator-${platform}`;
  const defaultName = 'onegini-sdk-configurator';

  if (environmentName) {
    console.log(`Using SDK Configurator executable in '${environmentName}' set in '${environmentVar}'`);
    return environmentName;
  }

  if (hasExtractedConfig) {
    console.log(`Using SDK Configurator executable in '${extractedName}'`);
    return extractedName;
  }

  console.log('Using SDK Configurator from $PATH');
  return defaultName;
}

function copyArtifactoryCredentials(projectRoot, platform, hasExtractedConfig) {
  return new Promise(function (resolve, reject) {
    if (platform === 'android' && hasExtractedConfig) {
      const filePath = `${projectRoot}/plugins/${extractedConfigPlugin}/artifactory.properties`;
      const destinationFilePath = path.join(projectRoot, 'platforms/android/artifactory.properties');
      const destDir = path.dirname(destinationFilePath);

      if (fs.existsSync(filePath) && fs.existsSync(destDir)) {
        console.log(`Copying '${filePath}' into '${destDir}'`);
        const stream = fs.createReadStream(filePath).pipe(fs.createWriteStream(destinationFilePath));
        stream.on('end', () => resolve("finished"));
        stream.on('error', (error) => reject("failed"));
        return;
      }
    }
    resolve("Skipping artifactory config");
  });
}

function hasExtractedConfigFiles(context) {
  return context.opts.cordova.plugins.indexOf(extractedConfigPlugin) > -1;
}
