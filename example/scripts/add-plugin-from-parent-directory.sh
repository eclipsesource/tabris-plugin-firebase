#!/bin/bash
set -e

# Add plugin from parent directory manually before Cordova does to workaround https://github.com/apache/cordova-cli/issues/401.

example_dir=`pwd`/../..
plugin_dir=$example_dir/..
package_name=`node -p "require('$plugin_dir/package.json').name"`
echo "add-plugin-from-parent-directory.sh: adding plugin '${package_name}' from parent directory manually to workaround apache/cordova-cli#401..."
mkdir -p ./plugins
rm -rf ./plugins/$package_name
cd 'plugins'
npm pack ${plugin_dir}
tar zxf $package_name*.tgz
mv package $package_name
rm $package_name*.tgz
echo "add-plugin-from-parent-directory.sh: successfully added plugin ${package_name} from parent directory manually."
