#![feature(box_syntax)]
#![feature(ptr_const_cast)]
extern crate wasm_bindgen;

use image::io::Reader as ImageReader;
use lazy_static::lazy_static;
use observable_rs::Observable;
use std::io::Cursor;
use std::ptr::write;
use std::sync::RwLock;
use wasm_bindgen::prelude::*;
use winit::event_loop::EventLoop;

mod analysis;
mod render;

pub enum RenderEvent {
    RequestRedraw,
}

lazy_static! {
    static ref ANALYZED_IMAGE: RwLock<Option<analysis::AnalyzedImage>> = RwLock::new(None);
    static ref RENDER_BUS: Observable<RenderEvent> = Observable::default<RenderEvent>();
}

#[wasm_bindgen(start)]
pub fn main() -> Result<(), JsValue> {
    std::panic::set_hook(Box::new(console_error_panic_hook::hook));
    wasm_logger::init(wasm_logger::Config::default());

    Ok(())
}

#[wasm_bindgen]
pub fn analyze_file(buffer: Vec<u8>) -> Result<(), JsValue> {
    let img = ImageReader::new(Cursor::new(buffer))
        .with_guessed_format()?
        .decode()?;

    let result = analysis::analyzer::analyze(&img);

    let mut writer = ANALYZED_IMAGE.write().unwrap();
    *writer = Some(result);

    RENDER_BUS.set(RenderEvent::RequestRedraw);

    Ok(())
}

#[wasm_bindgen]
pub fn init_renderer() -> Result<(), JsValue> {
    let event_loop = EventLoop::new();
    let window = winit::window::Window::new(&event_loop).unwrap();
    use winit::platform::web::WindowExtWebSys;

    web_sys::window()
        .and_then(|win| win.document())
        .and_then(|doc| doc.get_element_by_id("quadmage-view-container"))
        .and_then(|container| {
            let canvas = window.canvas();
            let canvas_style = canvas.style();
            canvas_style.set_property("width", "100%").ok();
            canvas_style.set_property("height", "100%").ok();
            canvas_style.set_property("outline", "none").ok();

            container.append_child(&web_sys::Element::from(canvas)).ok()
        })
        .expect("couldn't append canvas to #quadmage-view-container");
    wasm_bindgen_futures::spawn_local(render::run(window));

    Ok(())
}
