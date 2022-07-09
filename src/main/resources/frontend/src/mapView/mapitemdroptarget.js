import React from "react";
import Styles from "./mapitemdroptarget.module.css"

class MapItemDropTarget extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            dragging: false
        };
    }

    render() {
        return <div className={[Styles.DropTarget, this.state.dragging ? Styles.Dragging : ""].join(" ")} onDragEnter={this.dragEnter.bind(this)} onDrop={this.drop.bind(this)} onDragLeave={this.dragLeave.bind(this)} onDragOver={this.dragOver.bind(this)}>
            <span>{this.props.children}</span>
        </div>
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
        this.props.onClick();
    }
}

export default MapItemDropTarget;