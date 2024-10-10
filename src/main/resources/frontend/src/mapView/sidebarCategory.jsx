import React from "react";

import Styles from "./sidebarCategory.module.css";
import MapCategory from "./mapCategory";
import MapCollection from "./mapCollection";

class SidebarCategory extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            dragging: false,
        };
    }

    calculateClass() {
        let classes = [Styles.SidebarCategory];
        if (this.state.dragging) classes.push(Styles.Dragging)
        return classes.join(" ");
    }

    render() {
        return <a className={Styles.SidebarLink} href={`#${this.props.isCollection ? MapCollection.collectionLinkName(this.props.category) : MapCategory.categoryLinkName(this.props.category)}`} onDragEnter={this.dragEnter.bind(this)} onDrop={this.drop.bind(this)} onDragLeave={this.dragLeave.bind(this)} onDragOver={this.dragOver.bind(this)}>
            <div className={this.calculateClass()}>
                {MapCategory.categoryName(this.props.category)}
            </div>
        </a>
    }


    dragEnter(e) {
        e.preventDefault();
        e.stopPropagation();
        let transfer = e.dataTransfer;
        transfer.dropEffect = "copy";
    }

    dragLeave(e) {
        e.preventDefault();
        e.stopPropagation();

        this.setState({
            dragging: false
        });
    }

    dragOver(e) {
        e.preventDefault();
        e.stopPropagation();

        this.setState({
            dragging: true
        });
    }

    drop(e) {
        e.preventDefault();
        e.stopPropagation();

        this.setState({
            dragging: false
        });

        let transfer = e.dataTransfer;

        if (this.props.isCollection) {
            if (transfer.types.includes("application/x.vicr123.iomfrontend.mapid")) {
                //Adding to collection
                this.props.manager.addToCollection(transfer.getData("application/x.vicr123.iomfrontend.mapid"), this.props.category);
            }
        } else {
            if (transfer.types.includes("application/x.vicr123.iomfrontend.mapid")) {
                if (transfer.getData("application/x.vicr123.iomfrontend.maporigin") === "category") {
                    //Moving between categories
                    this.props.manager.setCatg(transfer.getData("application/x.vicr123.iomfrontend.mapid"), this.props.category);
                }
            } else {
                //Dropping files
                this.props.manager.uploadMaps(
                    [...transfer.items].filter(item => item.kind === "file")
                        .map(item => item.getAsFile())
                        .filter(file => ["image/png", "image/svg+xml", "image/gif", "image/webp"].includes(file.type)), this.props.category);
            }
        }
    }
}

export default SidebarCategory;