use crate::analysis::quadtree::QuadTree;
use image::RgbImage;

pub mod analyzer;
pub mod quadtree;

pub struct AnalyzedImage {
    tree: QuadTree,
    image: RgbImage,
    distance_min: f32,
    distance_max: f32,
}
