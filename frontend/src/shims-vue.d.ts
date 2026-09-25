declare module '*.vue' {
  import Vue from 'vue'

  export default Vue
}

declare module 'element-ui' {
  import Vue from 'vue'

  const ElementUI: {
    install: (vue: typeof Vue) => void
  }
  export default ElementUI
}
