import { boot } from 'quasar/wrappers';

interface WasmInterface {
  init_renderer: () => void;
}

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $wasm: WasmInterface;
  }
}


export default boot(({ app }) => {
  import('app/src-wasm/pkg').then(wasm => {
    app.config.globalProperties.$wasm = {
      init_renderer: wasm.init_renderer
    }
  });
});
