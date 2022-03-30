use super::quadtree;
use crate::analysis::quadtree::{BoxPx, PointPx, Quad, QuadMeta, QuadTree, SizePx};
use crate::analysis::AnalyzedImage;
use image::{DynamicImage, GenericImage, GenericImageView, Pixel, Rgb, Rgba};
use palette::{ColorDifference, FromColor, Lab, Srgb};
use std::cmp::{max, min};
use std::collections::HashMap;
use std::hash::{Hash, Hasher};

struct PixelPair {
    a: PointPx,
    b: PointPx,
}

impl PixelPair {
    pub fn new(a: PointPx, b: PointPx) -> Self {
        Self { a, b }
    }
}

impl Hash for PixelPair {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.a.hash(state);
        self.b.hash(state);
    }
}

fn normalize_color_components(color: &Rgb<u8>) -> (f32, f32, f32) {
    let components = color.0;
    (
        components[0] as f32 / 255 as f32,
        components[1] as f32 / 255 as f32,
        components[2] as f32 / 255 as f32,
    )
}

fn convert_to_lab(color: &Rgb<u8>) -> Lab {
    Lab::from_color(Srgb::from_components(normalize_color_components(color)).into_linear())
}

pub fn analyze(image: &DynamicImage) -> AnalyzedImage {
    let rgb_image = image.into_rgb8();
    let (width, height) = rgb_image.dimensions();
    let mut tree = QuadTree::new(BoxPx::from_origin_and_size(
        PointPx::new(0, 0),
        SizePx::new(width, height),
    ));

    for (x, y, color) in rgb_image.enumerate_pixels() {
        tree.insert(PointPx::new(x, y), convert_to_lab(color));
    }

    let mut pixel_distance_cache: HashMap<PixelPair, f32> = HashMap::new();
    let mut distance_min: f32 = 0.;
    let mut distance_max: f32 = 0.;
    for (bbox, quad_meta) in tree.get_mut_meta_map().iter() {
        let mut meta_distance: f64 = 0.;
        for y_a in bbox.y_range() {
            for x_a in bbox.x_range() {
                let point_a = PointPx::new(x_a, y_a);
                for y_b in bbox.y_range() {
                    for x_b in bbox.x_range() {
                        // skip same pixel
                        if x_b == x_a && y_b == y_a {
                            continue;
                        }

                        let pair = PixelPair::new(point_a, PointPx::new(x_b, y_b));
                        let new_distance = pixel_distance_cache.get(&pair).unwrap_or(&0.)
                            + convert_to_lab(&rgb_image.get_pixel(x_a, y_a)).get_color_difference(
                                &convert_to_lab(&rgb_image.get_pixel(x_b, y_b)),
                            );

                        pixel_distance_cache.insert(pair, new_distance);
                        meta_distance += new_distance;
                    }
                }
            }
        }
        let new_distance = (meta_distance / (bbox.size().area() as f64)) as f32;
        quad_meta.as_mut().set_distance(new_distance);
        distance_min = min(distance_min, new_distance);
        distance_max = max(distance_max, new_distance)
    }

    AnalyzedImage {
        tree,
        image: rgb_image,
        distance_min,
        distance_max,
    }
}
