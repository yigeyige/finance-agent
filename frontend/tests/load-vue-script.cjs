/** 测试专用 SFC 脚本加载器，服务端口可控，组件逻辑使用真实 Vue 构造器。 */
const fs = require('node:fs')
const path = require('node:path')
const Module = require('node:module')
const ts = require('typescript')

/** 编译并加载 Vue 文件的脚本部分，保留相对模块解析并显式隔离外部 API。 */
module.exports = function loadVueScript(relativePath, ports = {}) {
  const filename = path.resolve(__dirname, relativePath)
  const source = fs.readFileSync(filename, 'utf8').match(/<script lang="ts">([\s\S]*?)<\/script>/)[1]
  const loaded = new Module(filename, module)
  loaded.filename = filename
  loaded.paths = Module._nodeModulePaths(path.dirname(filename))
  const originalRequire = loaded.require.bind(loaded)
  loaded.require = name => Object.hasOwn(ports, name) ? ports[name] : originalRequire(name)
  loaded._compile(ts.transpileModule(source, { compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2018, esModuleInterop: true } }).outputText, filename)
  return loaded.exports.default
}
